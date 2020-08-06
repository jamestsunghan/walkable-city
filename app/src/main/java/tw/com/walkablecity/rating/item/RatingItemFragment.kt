package tw.com.walkablecity.rating.item


import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import tw.com.walkablecity.*
import tw.com.walkablecity.util.Util.getColor

import tw.com.walkablecity.data.PhotoPoint
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.databinding.FragmentRatingItemBinding
import tw.com.walkablecity.ext.getCroppedBitmap
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toLatLngPoints
import tw.com.walkablecity.rating.RatingType
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.WorkaroundMapFragment

class RatingItemFragment(
    private val type: RatingType, private val route: Route?
    , private val walk: Walk, private val photoPoints: List<PhotoPoint>?
) : Fragment(),
    OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {

    private lateinit var mapFragment: WorkaroundMapFragment

    private lateinit var map: GoogleMap

    private lateinit var polylineWalk: Polyline

    private lateinit var polylineToSend: Polyline

    val viewModel: RatingItemViewModel by viewModels {
        getVMFactory(route, walk, type, photoPoints)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return

        when (requireContext().resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                try {
                    val success = map.setMapStyle(
                        MapStyleOptions
                            .loadRawResourceStyle(requireContext(), R.raw.style_night_with_label)
                    )
                    if (!success) {
                        Logger.e("JJ_map style parsing fail")
                    }
                } catch (e: Resources.NotFoundException) {
                    Logger.e("JJ_map Can't find style. Error: $e")
                }
            }
            else -> {
                try {
                    val success = map.setMapStyle(MapStyleOptions("[]"))
                    if (!success) {
                        Logger.e("JJ_map style parsing fail")
                    }
                } catch (e: Resources.NotFoundException) {
                    Logger.e("JJ_map Can't find style. Error: $e")
                }
            }
        }

    }

    override fun onSnapshotReady(bitmap: Bitmap?) {
        if (type == RatingType.WALK) {
            viewModel.getImageUrl(
                walk,
                requireNotNull(UserManager.user?.idCustom),
                bitmap as Bitmap
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRatingItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_rating_item, container, false)

        binding.createRouteSlider.apply {
            addOnChangeListener { slider, _, _ ->
                viewModel.setCreateFilter(slider.values)
            }
            values = listOf(0f, walk.waypoints.lastIndex.toFloat())
            valueFrom = 0f
            valueTo = walk.waypoints.lastIndex.toFloat()
        }

        mapFragment.setListener(object : WorkaroundMapFragment.OnTouchListener {
            override fun onTouch() {
                binding.ratingScroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerPhotoPoint.adapter = RatingItemPhotoAdapter(route, type)

        viewModel.sendRating.observe(viewLifecycleOwner, Observer { sent ->
            if (sent) {
                val result = 1
                this.setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to result))
            }
        })

        when (type) {
            RatingType.ROUTE -> {
                route?.let {
                    mapFragment.getMapAsync { map ->
                        val pointList = if (route.waypoints.isNullOrEmpty()) {
                            route.waypointsLatLng
                        } else {
                            route.waypoints.toLatLngPoints()
                        }
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pointList[0], 15f))
                        map.addPolyline(PolylineOptions().addAll(pointList))

                        val points = route.photopoints
                        if (points.isNullOrEmpty()) {
                            Logger.d("JJ_photo we don't have points this time")
                        } else {
                            for (item in points) {
                                val latLng = item.getLatLngPoint()
                                val bitmap = item.drawBitmap(25)

                                map.addMarker(
                                    MarkerOptions().position(requireNotNull(latLng)).icon(
                                        BitmapDescriptorFactory.fromBitmap(bitmap.getCroppedBitmap())
                                    )
                                )
                            }
                        }
                    }

                    childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

                    binding.sendRating.setOnClickListener {
                        viewModel.sendRouteRating()
                    }
                }

            }
            RatingType.WALK -> {

                mapFragment.getMapAsync { map ->

                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            walk.waypoints.toLatLngPoints()[0],
                            15f
                        )
                    )
                    polylineWalk = map.addPolyline(
                        PolylineOptions().color(getColor(R.color.grey_transparent))
                            .addAll(walk.waypoints.toLatLngPoints())
                    )
                    polylineToSend =
                        map.addPolyline(PolylineOptions().addAll(walk.waypoints.toLatLngPoints()))


                    val points = photoPoints
                    if (points.isNullOrEmpty()) {
                        Logger.d("JJ_photo we don't have points this time")
                    } else {
                        for (item in points) {

                            val latLng = item.getLatLngPoint()
                            val bitmap = item.drawBitmap(25)

                            map.addMarker(
                                MarkerOptions().position(requireNotNull(latLng)).icon(
                                    BitmapDescriptorFactory.fromBitmap(bitmap.getCroppedBitmap())
                                )
                            )
                        }
                    }


                }


                viewModel.walkCreatePoints.observe(viewLifecycleOwner, Observer {
                    it?.let { latLngs ->
                        mapFragment.getMapAsync { map ->

                            polylineToSend.remove()
                            polylineToSend = map.addPolyline(
                                PolylineOptions()
                                    .color(getColor(R.color.red_heart_c73e3a))
                                    .addAll(latLngs)
                            )

                        }
                    }
                })

                childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

                binding.sendRating.setOnClickListener {
                    polylineWalk.remove()
                    map.snapshot(this)
                }

            }


        }

        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.uploadPhotoPoints(
                    viewModel.photoPoints,
                    viewModel.walk,
                    requireNotNull(UserManager.user?.id)
                )
            }
        })

        viewModel.uploadPointsSuccess.observe(viewLifecycleOwner, Observer {
            it?.let { uploaded ->
                if (uploaded) {
                    viewModel.sendRouteRating()
                }
            }
        })

        viewModel.ratingCoverage.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("coverage $it")
            }
        })

        viewModel.ratingScenery.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("scenery $it")
            }
        })

        viewModel.ratingRest.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("rest $it")
            }
        })

        viewModel.ratingSnack.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("snack $it")
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapFragment = WorkaroundMapFragment().apply {
            getMapAsync(this@RatingItemFragment)
        }
    }

    companion object {
        const val REQUEST_KEY = "navigation"
        const val BUNDLE_KEY = "sent"
    }
}

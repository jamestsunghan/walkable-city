package tw.com.walkablecity.rating.item


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import tw.com.walkablecity.*

import tw.com.walkablecity.data.PhotoPoint
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.databinding.FragmentRatingItemBinding
import tw.com.walkablecity.ext.getCroppedBitmap
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toLatLngPoints
import tw.com.walkablecity.rating.RatingType
import java.io.File

class RatingItemFragment(private val type: RatingType, private val route: Route?
                         , private val walk: Walk, private val photoPoints: List<PhotoPoint>?) : Fragment(),
    OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {

    private lateinit var mapFragment: WorkaroundMapFragment
    private lateinit var map: GoogleMap

    val viewModel: RatingItemViewModel by viewModels{
        getVMFactory(route, walk , type, photoPoints)}

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return

    }

    override fun onSnapshotReady(bitmap: Bitmap?) {
        if(type == RatingType.WALK){
            viewModel.getImageUrl(walk, requireNotNull(UserManager.user?.idCustom),bitmap as Bitmap)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRatingItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_rating_item, container, false)

        mapFragment.setListener(object: WorkaroundMapFragment.OnTouchListener{
            override fun onTouch() {
                binding.ratingScroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerPhotoPoint.adapter = RatingItemPhotoAdapter(route, type)

        viewModel.sendRating.observe(viewLifecycleOwner, Observer {
            if(it){
                val result = 1
                this.setFragmentResult("navigation",bundleOf("sent" to result))
            }
        })

        when(type){
            RatingType.ROUTE->{
                route?.let{
                    mapFragment.getMapAsync {map ->
                        val pointList = if(route.waypoints.isNullOrEmpty()){
                            route.waypointsLatLng
                        }else{
                            route.waypoints.toLatLngPoints()
                        }
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pointList[0],15f))
                        map.addPolyline(PolylineOptions().addAll(pointList))

                        val points = route.photopoints
                        if(points.isNullOrEmpty()){
                            Logger.d("JJ_photo we don't have points this time")
                        }else{
                            for(item in points){
                                val latLng = LatLng(requireNotNull(item.point).latitude, item.point.longitude)
                                val bitmap = BitmapFactory.decodeFile(item.photo, BitmapFactory.Options().apply {
                                    inSampleSize = 25
                                })

                                map.addMarker(MarkerOptions().position(latLng).icon(
                                    BitmapDescriptorFactory.fromBitmap(bitmap.getCroppedBitmap())))
                            }
                        }
                    }

                    childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

                    binding.sendRating.setOnClickListener {
                        viewModel.sendRouteRating()
                    }
                }

            }
            RatingType.WALK->{
                mapFragment.getMapAsync {

                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(walk.waypoints.toLatLngPoints()[0],15f))
                    it.addPolyline(PolylineOptions().addAll(walk.waypoints.toLatLngPoints()))

                    val points = photoPoints
                    if(points.isNullOrEmpty()){
                       Logger.d("JJ_photo we don't have points this time")
                    }else{
                        for(item in points){
                            val latLng = LatLng(requireNotNull(item.point).latitude, item.point.longitude)
                            val bitmap = BitmapFactory.decodeFile(item.photo, BitmapFactory.Options().apply {
                                inSampleSize = 25
                            })

                            it.addMarker(MarkerOptions().position(latLng).icon(
                                BitmapDescriptorFactory.fromBitmap(bitmap.getCroppedBitmap())))
                        }
                    }


                }

                childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

                binding.sendRating.setOnClickListener {
                    map.snapshot(this)
                }

            }
        }

        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            it?.let{
                viewModel.uploadPhotoPoints(viewModel.photoPoints, viewModel.walk, requireNotNull(UserManager.user?.id))
            }
        })

        viewModel.uploadPointsSuccess.observe(viewLifecycleOwner, Observer{
            it?.let{
                if(it){
                    viewModel.sendRouteRating()
                }
            }
        })








        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapFragment = WorkaroundMapFragment().apply{
            getMapAsync( this@RatingItemFragment )
        }
    }
}

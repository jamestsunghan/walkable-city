package tw.com.walkablecity.profile.explore


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import tw.com.walkablecity.util.Logger

import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.databinding.FragmentExploreBinding
import tw.com.walkablecity.ext.getCroppedBitmap
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toDateString
import tw.com.walkablecity.ext.toLatLngPoints
import tw.com.walkablecity.home.HomeFragment

class ExploreFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    private val viewModel: ExploreViewModel by viewModels { getVMFactory() }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        val mode =
            requireContext().resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        try {
            val success = when (mode) {
                Configuration.UI_MODE_NIGHT_YES ->
                    map.setMapStyle(
                        MapStyleOptions
                            .loadRawResourceStyle(requireContext(), R.raw.style_night_without_label)
                    )
                else ->
                    map.setMapStyle(
                        MapStyleOptions
                            .loadRawResourceStyle(requireContext(), R.raw.style_json)
                    )
            }
            if (!success) {
                Logger.e("JJ_map style parsing fail")
            }
        } catch (e: Resources.NotFoundException) {
            Logger.e("JJ_map Can't find style. Error: $e")
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != HomeFragment.REQUEST_LOCATION) return
        if (Util.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            viewModel.permissionGranted()
            viewModel.clientCurrentLocation()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                viewModel.permissionDeniedForever()
            }
            viewModel.permissionDenied()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentExploreBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_explore, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        if (ContextCompat.checkSelfPermission(
                WalkableApp.instance,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.permissionGranted()
            viewModel.clientCurrentLocation()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                viewModel.permissionDeniedForever()
            }
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                HomeFragment.REQUEST_LOCATION
            )
        }

        viewModel.userWalks.observe(viewLifecycleOwner, Observer {
            it?.let { walks ->
                viewModel.currentLocation.value?.let { latLng ->
                    mapFragment.getMapAsync { map ->

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))

                        for (walk in walks) {

                            map.addPolyline(
                                PolylineOptions()
                                    .color(WalkableApp.instance.getColor(R.color.walk_path))
                                    .width(20f)
                                    .addAll(walk.waypoints.toLatLngPoints())
                            )
                            if (walk.waypoints.isNotEmpty()) {
                                val bitmapRealDimens = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    25f,
                                    WalkableApp.instance.resources.displayMetrics
                                ).toInt()
                                val drawable = WalkableApp.instance.resources.getDrawable(
                                    R.mipmap.ic_launcher_foot_in_white,
                                    WalkableApp.instance.theme
                                )
                                val bitmap = drawable.toBitmap(bitmapRealDimens, bitmapRealDimens)
                                map.addMarker(
                                    MarkerOptions()
                                        .position(walk.waypoints.toLatLngPoints()[0])
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap.getCroppedBitmap()))
                                        .title(walk.startTime?.toDateString())
                                        .snippet(
                                            String.format(
                                                getString(R.string.explore_walk_distance),
                                                walk.distance
                                            )
                                        )
                                )
                            }
                        }
                        map.setInfoWindowAdapter(ExploreInfoWindowAdapter(walks, container))
                    }
                }

            }
        })

        viewModel.currentLocation.observe(viewLifecycleOwner, Observer { latLng ->
            latLng?.let {
                viewModel.userWalks.value?.let { walks ->
                    mapFragment.getMapAsync { map ->

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

                        for (walk in walks) {

                            map.addPolyline(
                                PolylineOptions().color(WalkableApp.instance.getColor(R.color.red_heart_c73e3a))
                                    .addAll(walk.waypoints.toLatLngPoints())
                            )

                        }
                    }
                }

                childFragmentManager.beginTransaction().replace(R.id.explore_map, mapFragment).commit()
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapFragment = SupportMapFragment().apply {
            getMapAsync(this@ExploreFragment)
        }
    }
}

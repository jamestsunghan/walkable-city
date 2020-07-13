package tw.com.walkablecity.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import tw.com.walkablecity.MainActivity
import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getColor
import tw.com.walkablecity.Util.isPermissionGranted
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.Util.requestPermission
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.databinding.FragmentHomeBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toLatLngPoints

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener{



    lateinit var viewModelInit: HomeViewModel

    private var permissionDenied =  false
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    companion object{
        const val REQUEST_LOCATION = 0x00
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)

    }

    override fun onMyLocationClick(location: Location) {

    }

    override fun onMyLocationButtonClick(): Boolean {
        makeShortToast(R.string.my_location)
        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode != REQUEST_LOCATION) return
        if(isPermissionGranted(permissions,grantResults, Manifest.permission.ACCESS_FINE_LOCATION)){
            viewModelInit.clientCurrentLocation()
        }else{
            permissionDenied = true
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding = DataBindingUtil
            .inflate(inflater,
                R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this


        val route = if((arguments as Bundle).containsKey("routeKey")){
            HomeFragmentArgs.fromBundle(arguments as Bundle).routeKey
        }else{
            null
        }

        val viewModel: HomeViewModel by viewModels{getVMFactory(route)}

        viewModelInit = viewModel

        binding.viewModel = viewModel


        if(checkSelfPermission(WalkableApp.instance, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            viewModel.clientCurrentLocation()
        }else{
//            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
//                Toast.makeText(WalkableApp.instance,"Location permission is needed for route recording and suggestion routes nearby.",
//                    Toast.LENGTH_SHORT).show()
//            }
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
//            requestPermission(requireActivity() as MainActivity, REQUEST_LOCATION
//                , Manifest.permission.ACCESS_FINE_LOCATION,true)
        }


        viewModel.navigateToRating.observe(viewLifecycleOwner, Observer {
            it?.let{
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRatingFragment(viewModel.route.value, it))
                viewModel.navigateToRatingComplete()
            }
        })

        viewModel.navigateToLoad.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoadRouteFragment())
                viewModel.navigateToLoadComplete()
            }
        })

        viewModel.navigateToSearch.observe(viewLifecycleOwner, Observer {
            if(it){
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
                viewModel.navigateToSearchComplete()
            }
        })

        viewModel.route.observe(viewLifecycleOwner, Observer {
            it?.let{
                Log.d("JJ", " route update $it")
            }
        })

        viewModel.walkerDistance.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.d("JJ","distance $it")
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer{
            it?.let{
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        viewModel.currentLocation.observe(viewLifecycleOwner, Observer {geoPoint ->
            geoPoint?.let{

                mapFragment.getMapAsync {
                    val currentLocation = LatLng(geoPoint.latitude, geoPoint.longitude)

                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15f))
                    it.uiSettings.isMyLocationButtonEnabled = true
                    it.isMyLocationEnabled = true
                    if(route!=null){
                        viewModel.drawPath(currentLocation, currentLocation, route.waypoints.toLatLngPoints())
                    }
                }


                childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
            }
        })

        viewModel.mapRoute.observe(viewLifecycleOwner, Observer{
            it?.let{
                mapFragment.getMapAsync {map ->
                    Log.d("JJ","direction result $it")
                    Log.d("JJ","duration ${it.routes[0].legs.map{leg -> leg.distance}}")

                    map.addPolyline(PolylineOptions().color(getColor(R.color.secondaryLightColor)).addAll(PolyUtil.decode(it.routes[0].overviewPolyline?.points)))
                }
            }
        })

        viewModel.trackGeoPoints.observe(viewLifecycleOwner,Observer{
            it?.let{list->
                mapFragment.getMapAsync{map->
                    map.addPolyline(PolylineOptions().color(getColor(R.color.secondaryDarkColor)).addAll(list.toLatLngPoints()))
                    viewModel.circle.value?.let{circle ->
                        map.addCircle(circle)
                    }
                }
            }
        })



        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapFragment = SupportMapFragment().apply{
            getMapAsync(this@HomeFragment)
        }

    }


}

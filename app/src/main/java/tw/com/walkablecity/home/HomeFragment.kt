package tw.com.walkablecity.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.Timestamp.now
import com.google.maps.android.PolyUtil
import tw.com.walkablecity.*
import tw.com.walkablecity.util.Util.getColor
import tw.com.walkablecity.util.Util.isPermissionGranted
import tw.com.walkablecity.util.Util.makeShortToast
import tw.com.walkablecity.util.Util.showBadgeDialog
import tw.com.walkablecity.databinding.FragmentHomeBinding
import tw.com.walkablecity.ext.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener{

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    val viewModel: HomeViewModel by viewModels{
        getVMFactory(HomeFragmentArgs.fromBundle(arguments as Bundle).routeKey
            , HomeFragmentArgs.fromBundle(arguments as Bundle).destinationKey)
    }

    companion object{
        const val REQUEST_LOCATION      = 0x00
        const val REQUEST_IMAGE_CAPTURE = 0x01
        const val REQUEST_CAMERA        = 0x02
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        when(requireContext().resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)){
            Configuration.UI_MODE_NIGHT_YES ->{
                try{
                    val success = map.setMapStyle(MapStyleOptions
                        .loadRawResourceStyle(requireContext(), R.raw.style_night_with_label))
                    if(!success){
                        Logger.e("JJ_map style parsing fail")
                    }
                }catch (e: Resources.NotFoundException){
                    Logger.e("JJ_map Can't find style. Error: $e")
                }
            }
            else ->{
                try{
                    val success = map.setMapStyle(MapStyleOptions("[]"))
                    if(!success){
                        Logger.e("JJ_map style parsing fail")
                    }
                }catch (e: Resources.NotFoundException){
                    Logger.e("JJ_map Can't find style. Error: $e")
                }
            }
        }


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
        if(!(requestCode == REQUEST_LOCATION || requestCode == REQUEST_CAMERA)) return
        if(isPermissionGranted(permissions,grantResults, Manifest.permission.ACCESS_FINE_LOCATION)){
            viewModel.permissionGranted()
            viewModel.clientCurrentLocation()
        }else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                viewModel.permissionDeniedForever()
            }
            viewModel.permissionDenied()
        }

    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        UserManager.user?.id?.let{
            mainViewModel.getInvitation(it)
            Logger.d("badge event dialog from home")
            mainViewModel.getUserEventCount(it)
            mainViewModel.getUserFriendCount(it)
        }


        val binding: FragmentHomeBinding = DataBindingUtil
            .inflate(inflater,
                R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this


        binding.viewModel = viewModel

        viewModel.argument?.apply {
            this.waypointsLatLng = this.waypoints.toLatLngPoints()
            this.waypoints = listOf()
        }
        checkPermission()

        binding.permissionDialogButton.setOnClickListener {
            when(viewModel.dontAskAgain.value){
                true ->{
                    val intent = Intent()
                        .setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package",requireContext().packageName, null))
                    startActivity(intent)

                }
                else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
            }
        }

        binding.takePicture.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.dayNightSwitch.setOnClickListener {
            when(requireContext().resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)){
                Configuration.UI_MODE_NIGHT_YES ->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                else ->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }


        viewModel.navigateToRating.observe(viewLifecycleOwner, Observer {
            it?.let{
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreateRouteDialogFragment(viewModel.route.value, it, viewModel.photopoints.value?.toTypedArray()))
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
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                    requireNotNull(viewModel.currentLocation.value)))
                viewModel.navigateToSearchComplete()
            }
        })



        viewModel.error.observe(viewLifecycleOwner, Observer{
            it?.let{
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
        var previousUpgrade = 0
        viewModel.upgrade.observe(viewLifecycleOwner, Observer{
            it?.let{grade->
                Logger.d("let see some grade $grade")
                if(grade > previousUpgrade){
                    mainViewModel.addToBadgeTotal(grade, R.id.homeFragment)
                    val dialog = showBadgeDialog(grade, requireContext(), findNavController(),
                        HomeFragmentDirections.actionGlobalBadgeFragment()
                        , getString(R.string.badge_dialog_walk))

                    dialog.show()
                    previousUpgrade = grade

                }
            }
        })

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        //observe check area

//        viewModel.dontAskAgain.observe(viewLifecycleOwner, Observer{
//            Logger.d("dont ask again ${it ?: "null"}")
//        })
//
//        viewModel.route.observe(viewLifecycleOwner, Observer {
//            it?.let{
//                Logger.d(" route update $it")
//            }
//        })
//
//        viewModel.walkerDistance.observe(viewLifecycleOwner, Observer{
//            it?.let{
//                Logger.d("distance $it")
//            }
//        })

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        viewModel.currentLocation.observe(viewLifecycleOwner, Observer {
            it?.let{latLng->

                mapFragment.getMapAsync {map->

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
                    map.uiSettings.isMyLocationButtonEnabled = true
                    map.isMyLocationEnabled = true
                    val route = viewModel.argument
                    if(route!=null){
                        viewModel.drawPath(latLng, viewModel.destination ?: latLng, route.waypointsLatLng)
                    }
                }


                childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
            }
        })

        viewModel.startLocation.observe(viewLifecycleOwner, Observer{
            it?.let{latLng->
                Logger.d("start location $latLng")
                viewModel.addStartTrackPoint(latLng)
            }
        })

        viewModel.mapRoute.observe(viewLifecycleOwner, Observer{
            it?.let{
                mapFragment.getMapAsync {map ->
                    Logger.d("direction result $it")
                    if(it.routes.isNotEmpty()){
                        Logger.d("duration ${it.routes[0].legs.map{leg -> leg.distance}}")

                        map.addPolyline(PolylineOptions()
                            .color(getColor(R.color.secondaryLightColor))
                            .addAll(PolyUtil.decode(it.routes[0].overviewPolyline?.points)))
                    }

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

        viewModel.photopoints.observe(viewLifecycleOwner, Observer{
            it?.let{points->

                if(points.isNotEmpty()){
                    mapFragment.getMapAsync {markerMap->
                        for(item in points){
                            val latLng = LatLng(requireNotNull(item.point).latitude, item.point.longitude)
                            val file = File(item.photo)
                            val imageUri = FileProvider.getUriForFile(WalkableApp.instance, WalkableApp.instance.packageName + ".provider", file)
//                            val stream = WalkableApp.instance.contentResolver.openInputStream(imageUri)
                            val bitmap = BitmapFactory.decodeFile(item.photo, BitmapFactory.Options().apply {
                                inSampleSize = 25
                            })

//                            stream?.close()
                            Logger.d("JJ_camera file path ${item.photo}")
                            markerMap.addMarker(MarkerOptions().position(latLng).icon(
                                BitmapDescriptorFactory.fromBitmap(bitmap.getCroppedBitmap())))
                        }

                    }
                }

            }
        })
        var previousStatus:WalkerStatus? = null
        viewModel.walkerStatus.observe(viewLifecycleOwner, Observer{
            it?.let{status->
                mainViewModel.walkStatusCheck(status)
                if(status == WalkerStatus.WALKING && previousStatus != WalkerStatus.PAUSING){

                    binding.takePicture
                        .startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_fade_in))

                    binding.cardWalkZone
                        .startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_silde_up))

                }
                previousStatus = status
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val file = File(context?.cacheDir,"images")
            val pathName = "${file}/image${time.seconds}.jpg"
            val streamFile = File(pathName)

            val imageUri = FileProvider.getUriForFile(WalkableApp.instance, WalkableApp.instance.packageName + ".provider", streamFile)
            viewModel.addPhotoPoint(pathName)
            viewModel.cameraClicked.value = true
        }
    }

    var time = now()

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val file = File(context?.cacheDir,"images")
                time = now()
                val pathName = "${file}/image${time.seconds}.jpg"

                val photoFile: FileOutputStream? = try{
                    file.mkdir()
                    val stream = FileOutputStream("${file}/image${time.seconds}.jpg")
                    stream.flush()
                    stream.close()
                    stream
                }catch (e: IOException){
                    e.printStackTrace()
                    null
                }

                Logger.d("JJ_camera stream $photoFile")
                photoFile?.also {
                    val uri = FileProvider.getUriForFile(WalkableApp.instance, WalkableApp.instance.packageName + ".provider", File(pathName))
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }

            }
        }
    }

    private fun checkPermission(){
        if(checkSelfPermission(WalkableApp.instance, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            viewModel.permissionGranted()
            viewModel.clientCurrentLocation()
        }else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                viewModel.permissionDeniedForever()
            }
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }




}

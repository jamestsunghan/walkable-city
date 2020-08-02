package tw.com.walkablecity.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.Timestamp.now
import com.google.maps.android.PolyUtil
import com.google.maps.android.ktx.addMarker
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.internal.wait
import tw.com.walkablecity.*
import tw.com.walkablecity.Util.getColor
import tw.com.walkablecity.Util.isPermissionGranted
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.Util.requestPermission
import tw.com.walkablecity.Util.showBadgeDialog
import tw.com.walkablecity.Util.showWalkDistroyDialog
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.databinding.FragmentHomeBinding
import tw.com.walkablecity.ext.*
import tw.com.walkablecity.profile.ProfileFragmentDirections
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener{



    lateinit var viewModelInit: HomeViewModel

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

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
            viewModelInit.permissionGranted()
            viewModelInit.clientCurrentLocation()
        }else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                viewModelInit.permissionDeniedForever()
            }
            viewModelInit.permissionDenied()
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


        val route = if((arguments as Bundle).containsKey("routeKey")){
            HomeFragmentArgs.fromBundle(arguments as Bundle).routeKey
        }else{
            null
        }

        val destination = if((arguments as Bundle).containsKey("destinationKey")){
            HomeFragmentArgs.fromBundle(arguments as Bundle).destinationKey
        }else{
            null
        }

        val viewModel: HomeViewModel by viewModels{getVMFactory(route, destination)}


        viewModelInit = viewModel

        binding.viewModel = viewModel

        route?.apply {
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

//            if(checkSelfPermission(WalkableApp.instance, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//                viewModel.cameraPermissionGranted()
//                initializeCamera()
//            }else{
//                if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
////                    viewModel.permissionDeniedForever()
//                }
//                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
//
//            }
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
                    if(route!=null){
                        viewModel.drawPath(latLng, destination ?: latLng, route.waypointsLatLng)
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
            viewModelInit.addPhotoPoint(pathName)
            viewModelInit.cameraClicked.value = true
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

    private lateinit var camera: CameraDevice
    private val cameraManager: CameraManager by lazy{
        WalkableApp.instance.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private val args: HomeFragmentArgs by navArgs()
    private val cameraThread = HandlerThread("cameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)
    private val cameraIds = cameraManager.cameraIdList.filter{
        val characteristics = cameraManager.getCameraCharacteristics(it)
        val capabilities = characteristics.get(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
        )
        capabilities?.contains(
            CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) ?: false

    }.filter{id->
        val characteristics = cameraManager.getCameraCharacteristics(id)
        val orientation = lensOrientationString(characteristics.get(CameraCharacteristics.LENS_FACING)!!)
        orientation == "Back"
    }

    /** Helper function used to convert a lens orientation enum into a human-readable string */
    private fun lensOrientationString(value: Int) = when(value) {
        CameraCharacteristics.LENS_FACING_BACK -> "Back"
        CameraCharacteristics.LENS_FACING_FRONT -> "Front"
        CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
        else -> "Unknown"
    }

    fun initializeCamera() = lifecycleScope.launch {
        camera = openCamera(cameraManager, cameraIds[0], cameraHandler)
    }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(manager: CameraManager, cameraId: String, handler: Handler? = null)
            : CameraDevice = suspendCancellableCoroutine{ continuation->
        manager.openCamera(cameraId, object: CameraDevice.StateCallback(){
            override fun onOpened(camera: CameraDevice) = continuation.resume(camera)

            override fun onDisconnected(camera: CameraDevice) {
                Logger.w("JJ_camera Camera $cameraId has been disconnected")
                requireActivity().finish()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                val message = when(error){
                    ERROR_CAMERA_DEVICE -> "Fatal ${camera}"
                    ERROR_CAMERA_DISABLED ->"Device Policy"
                    ERROR_CAMERA_IN_USE ->"Camera in use"
                    ERROR_CAMERA_SERVICE ->"Fatal ${camera}"
                    ERROR_MAX_CAMERAS_IN_USE ->"Maximum cameras in use"
                    else ->"Unknown"
                }
                val exception = RuntimeException("Camera $cameraId error: $error $message")
                Logger.e("JJ_camera" + exception.message + exception)
                if(continuation.isActive) continuation.resumeWithException(exception)
            }
        }, handler)

    }

    fun checkPermission(){
        if(checkSelfPermission(WalkableApp.instance, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            viewModelInit.permissionGranted()
            viewModelInit.clientCurrentLocation()
        }else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                viewModelInit.permissionDeniedForever()
            }
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }




}

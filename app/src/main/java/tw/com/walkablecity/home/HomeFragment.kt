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
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util.trackTimer
import tw.com.walkablecity.util.Util.trackerPoints
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    val viewModel: HomeViewModel by viewModels {
        getVMFactory(
            HomeFragmentArgs.fromBundle(arguments as Bundle).routeKey
            , HomeFragmentArgs.fromBundle(arguments as Bundle).destinationKey
        )
    }

    companion object {
        const val REQUEST_LOCATION = 0x00
        const val REQUEST_IMAGE_CAPTURE = 0x01
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return

        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)

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
        if (requestCode != REQUEST_LOCATION) return
        if (isPermissionGranted(
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
        UserManager.user?.id?.let {
            mainViewModel.getInvitation(it)
            Logger.d("badge event dialog from home")
            mainViewModel.getUserEventCount(it)
            mainViewModel.getUserFriendCount(it)
        }

        val binding: FragmentHomeBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_home, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.argument?.apply {
            this.waypointsLatLng = this.waypoints.toLatLngPoints()
            this.waypoints = listOf()
        }

        checkPermission()

        binding.permissionDialogButton.setOnClickListener {
            if (viewModel.dontAskAgain.value == true) {
                val intent = Intent()
                    .setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", requireContext().packageName, null))
                startActivity(intent)
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION
                )
            }
        }

        binding.takePicture.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.dayNightSwitch.setOnClickListener {
            when (requireContext().resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }

        viewModel.navigateToRating.observe(viewLifecycleOwner, Observer {
            it?.let { walk ->
                findNavController().navigate(
                    HomeFragmentDirections
                        .actionHomeFragmentToCreateRouteDialogFragment(
                            viewModel.route.value, walk, viewModel.photoPoints.value?.toTypedArray()
                        )
                )
                viewModel.navigateToRatingComplete()
            }
        })

        viewModel.navigateToLoad.observe(viewLifecycleOwner, Observer { confirmed ->
            if (confirmed) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoadRouteFragment())
                viewModel.navigateToLoadComplete()
            }
        })

        viewModel.navigateToSearch.observe(viewLifecycleOwner, Observer { confirmed ->
            if (confirmed) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                        requireNotNull(viewModel.currentLocation.value)
                    )
                )
                viewModel.navigateToSearchComplete()
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            it?.let { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })

        var previousUpgrade = 0
        viewModel.upgrade.observe(viewLifecycleOwner, Observer {
            it?.let { grade ->
                Logger.d("let see some grade $grade")
                if (grade > previousUpgrade) {
                    mainViewModel.addToBadgeTotal(grade, R.id.homeFragment)
                    val dialog = showBadgeDialog(
                        grade, requireContext(), findNavController(),
                        HomeFragmentDirections.actionGlobalBadgeFragment()
                        , getString(R.string.badge_dialog_walk)
                    )

                    dialog.show()
                    previousUpgrade = grade
                }
            }
        })

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        viewModel.currentLocation.observe(viewLifecycleOwner, Observer {
            it?.let { latLng ->

                mapFragment.getMapAsync { map ->

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    map.uiSettings.isMyLocationButtonEnabled = true
                    map.isMyLocationEnabled = true
                    val route = viewModel.argument
                    if (route != null) {
                        viewModel.drawPath(
                            latLng,
                            viewModel.destination ?: latLng,
                            route.waypointsLatLng
                        )
                    }
                }

                childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
            }
        })

        viewModel.startLocation.observe(viewLifecycleOwner, Observer {
            it?.let { latLng ->
                Logger.d("start location $latLng")
                viewModel.addStartTrackPoint(latLng)
            }
        })

        viewModel.mapRoute.observe(viewLifecycleOwner, Observer {
            it?.let {direction->
                mapFragment.getMapAsync { map ->
                    Logger.d("direction result $direction")
                    if (direction.routes.isNotEmpty()) {
                        Logger.d("duration ${it.routes[0].legs.map { leg -> leg.distance }}")

                        map.addPolyline(
                            PolylineOptions()
                                .color(getColor(R.color.secondaryLightColor))
                                .addAll(PolyUtil.decode(direction.routes[0].overviewPolyline?.points))
                        )
                    }
                }
            }
        })

        viewModel.trackPoints.observe(viewLifecycleOwner, Observer {
            it?.let { list ->
                mapFragment.getMapAsync { map ->
                    map.addPolyline(
                        PolylineOptions()
                            .color(getColor(R.color.secondaryDarkColor)).addAll(list)
                    )
                }
            }
        })

        viewModel.photoPoints.observe(viewLifecycleOwner, Observer {
            it?.let { points ->

                if (points.isNotEmpty()) {
                    mapFragment.getMapAsync { markerMap ->

                        val lastPoint = points.last()

                        val latLng =
                            LatLng(requireNotNull(lastPoint.point).latitude, lastPoint.point.longitude)

                        val bitmap =
                            BitmapFactory.decodeFile(lastPoint.photo, BitmapFactory.Options().apply {
                                inSampleSize = 25
                            })

                        markerMap.addMarker(
                            MarkerOptions().position(latLng).icon(
                                BitmapDescriptorFactory.fromBitmap(bitmap.getCroppedBitmap())
                            )
                        )
                    }
                }
            }
        })

        trackTimer.observe(viewLifecycleOwner, Observer{
            it?.let{time->
                viewModel.setTrackerTimer(time)
            }
        })

        trackerPoints.observe(viewLifecycleOwner, Observer{list->
            if(list.isNotEmpty()){
                viewModel.setTrackerPoints(list)
            }
        })

        var previousStatus: WalkerStatus? = null
        viewModel.walkerStatus.observe(viewLifecycleOwner, Observer {
            it?.let { status ->
                mainViewModel.walkStatusCheck(status)

                if (status == WalkerStatus.WALKING && previousStatus != WalkerStatus.PAUSING) {

                    binding.takePicture.startAnimation(
                        AnimationUtils.loadAnimation(requireContext(), R.anim.anim_fade_in)
                    )

                    binding.cardWalkZone.startAnimation(
                        AnimationUtils.loadAnimation(requireContext(), R.anim.anim_silde_up)
                    )
                }
                previousStatus = status
            }
        })

        mainViewModel.cacheDeleted.observe(viewLifecycleOwner, Observer{
            it?.let{deleted->
                if(!deleted){
                    val deletion = viewModel.photoPoints.value?.let { list ->
                        for (item in list) {
                            val deletion = File(item.photo).delete()
                            Logger.d("is delete succeeded? $deletion")
                        }
                        true
                    } ?: true
                    mainViewModel.deletionCache(deletion)
                }
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapFragment = SupportMapFragment().apply {
            getMapAsync(this@HomeFragment)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val file = File(context?.cacheDir, "images")
            val pathName = "${file}/image${time.seconds}.jpg"

            viewModel.addPhotoPoint(pathName)
        }
    }

    var time = now()

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {

                val file = File(context?.cacheDir, "images")
                time = now()

                val pathName = "${file}/image${time.seconds}.jpg"

                val photoFile: FileOutputStream? = try {
                    file.mkdir()
                    val stream = FileOutputStream(pathName)
                    stream.flush()
                    stream.close()
                    stream
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }

                Logger.d("JJ_camera stream $photoFile")
                photoFile?.also {
                    val uri = FileProvider.getUriForFile(
                        WalkableApp.instance,
                        WalkableApp.instance.packageName + ".provider",
                        File(pathName)
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun checkPermission() {
        if (checkSelfPermission(
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
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }
}

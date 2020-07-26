package tw.com.walkablecity.home


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.*
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.getColor
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toDistance
import tw.com.walkablecity.ext.toGeoPoint
import java.lang.Exception
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume

class HomeViewModel(val walkableRepository: WalkableRepository, val argument: Route?, val destination: LatLng?): ViewModel(){

    companion object{
        const val UPDATE_INTERVAL_IN_MILLISECONDS = 5000L
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    val waypointLatLng = MutableLiveData<List<LatLng>>()

    private val _permissionDenied =  MutableLiveData<Boolean>(false)
    val permissionDenied: LiveData<Boolean> get() = _permissionDenied

    private val _cameraPermissionDenied =  MutableLiveData<Boolean>(false)
    val cameraPermissionDenied: LiveData<Boolean> get() = _cameraPermissionDenied

    private val fusedLocationClient = FusedLocationProviderClient(WalkableApp.instance)

    private val _checkPermission = MutableLiveData<Boolean>(false)
    val checkPermission: LiveData<Boolean> get() = _checkPermission

    private val _dontAskAgain = MutableLiveData<Boolean>(false)
    val dontAskAgain: LiveData<Boolean> get() = _dontAskAgain

    private val _navigateToLoad = MutableLiveData<Boolean>()
    val navigateToLoad: LiveData<Boolean> get() = _navigateToLoad

    private val _navigateToSearch = MutableLiveData<Boolean>()
    val navigateToSearch: LiveData<Boolean> get() = _navigateToSearch

    private val _navigateToRating = MutableLiveData<Walk>()
    val navigateToRating: LiveData<Walk> get() = _navigateToRating

    private val _walkerStatus = MutableLiveData<WalkerStatus>()
    val walkerStatus: LiveData<WalkerStatus> get() = _walkerStatus

    private val _loadStatus = MutableLiveData<LoadStatus>()
    val loadStatus: LiveData<LoadStatus> get() = _loadStatus

    private val _mapRoute = MutableLiveData<DirectionResult>()
    val mapRoute: LiveData<DirectionResult> get() = _mapRoute

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _photoPoints = MutableLiveData<MutableList<PhotoPoint>>(mutableListOf())
    val photopoints: LiveData<MutableList<PhotoPoint>> get() = _photoPoints



    val startTime = MutableLiveData<Timestamp>()

    val duration = MutableLiveData<Long>()

    val endTime = MutableLiveData<Timestamp>()

    val currentLocation = MutableLiveData<LatLng>()
    val startLocation = MutableLiveData<LatLng>()
    val trackPoints = MutableLiveData<MutableList<LatLng>>(mutableListOf())

    val walkerDistance = Transformations.map(trackPoints){
        if(it == null || it.isEmpty()){
            0F
        }else{
            it.toDistance()
        }
    }
    val walkerTimer = MutableLiveData<Long>(0L)
    val trackGeoPoints = Transformations.map(trackPoints){list ->
        list.map{GeoPoint(it.latitude, it.longitude)}
    }
    val circle = Transformations.map(currentLocation){
        it?.let{
            CircleOptions().center(it).radius(20.0).fillColor(getColor(R.color.blue_2e5c6e))
        }
    }

    private var handler = Handler()
    private lateinit var runnable: Runnable

    val route = MutableLiveData<Route>().apply {
        value = argument
    }
    private var locationCallback: LocationCallback

    val cameraClicked = MutableLiveData<Boolean>(false)

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{
        val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN).format(now().seconds.times(1000)).toLong()
        Log.d("JJ","date $date")

        _walkerStatus.value = WalkerStatus.PREPARE
//        clientCurrentLocation()
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                if(p0 != null && p0.lastLocation != null){
                    trackPoints.value = trackPoints.value?.plus(LatLng(p0.lastLocation.latitude, p0.lastLocation.longitude)) as MutableList<LatLng>

                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
            }
        }
    }

    fun permissionDeniedForever(){
        _dontAskAgain.value = true
    }

    fun permissionDenied(){
        _permissionDenied.value = true
    }

    fun permissionGranted(){
        _permissionDenied.value = false
    }

    fun cameraPermissionDenied(){
        _cameraPermissionDenied.value = true
    }

    fun cameraPermissionGranted(){
        _cameraPermissionDenied.value = false
    }

    fun navigateToLoad(){
        _navigateToLoad.value = true
    }
    fun navigateToLoadComplete(){
        _navigateToLoad.value = false
    }

    fun navigateToSearch(){
        _navigateToSearch.value = true
    }

    fun navigateToSearchComplete(){
        _navigateToSearch.value = false
    }

    fun navigateToRating(walk: Walk?){
        _navigateToRating.value = walk
    }

    fun navigateToRatingComplete(){
        _navigateToRating.value = null
        _walkerStatus.value = WalkerStatus.PREPARE

    }

    fun addPhotoPoint(url: String){
        val photoPoint = PhotoPoint(
            point = trackPoints.value?.last()?.toGeoPoint() ?: requireNotNull(currentLocation.value).toGeoPoint(),
            photo = url
        )
        _photoPoints.value = photopoints.value?.plus(photoPoint) as MutableList<PhotoPoint>
    }

    fun startStopSwitch(){
        when(walkerStatus.value){
            WalkerStatus.PREPARE -> startWalking()
            WalkerStatus.PAUSING -> stopWalking()
            WalkerStatus.WALKING -> stopWalking()
            WalkerStatus.FINISH  -> {}
        }
    }

    fun pauseResumeSwitch(){
        when(walkerStatus.value){
            WalkerStatus.PREPARE -> {}
            WalkerStatus.PAUSING -> resumeWalking()
            WalkerStatus.WALKING -> pauseWalking()
            WalkerStatus.FINISH  -> {}
        }
    }

    private fun startRecordingDistance(){

        fusedLocationClient.requestLocationUpdates(createLocationRequest(),locationCallback, Looper.getMainLooper())
    }

    private fun stopRecordingDistance() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun startWalking(){
        _walkerStatus.value = WalkerStatus.WALKING
        //GPS recording
        clientCurrentLocation()
        startTime.value = now()
        //timer start
        startTimer()

    }

    private fun pauseWalking(){
        _walkerStatus.value = WalkerStatus.PAUSING
        //timer pause
        handler.removeCallbacks(runnable)
        stopRecordingDistance()

    }

    private fun resumeWalking(){
        //timer resume
        startTimer()
        clientCurrentLocation()
        _walkerStatus.value = WalkerStatus.WALKING
    }

    private fun stopWalking(){
        _walkerStatus.value = WalkerStatus.FINISH
        stopRecordingDistance()
        //timer stop
        handler.removeCallbacks(runnable)
        endTime.value = now()
        //GPS stop recording

        //navigate to rating
        val walk = Walk(
            distance = walkerDistance.value as Float,
            duration = walkerTimer.value as Long,
            startTime = startTime.value as Timestamp,
            endTime = endTime.value as Timestamp,
            routeId = route.value?.id,
            waypoints = trackPoints.value?.map{it.toGeoPoint()} as List<GeoPoint>)
        updateWalk(walk)
    }

    private fun updateWalk(walk: Walk){

        coroutineScope.launch {

            _loadStatus.value = LoadStatus.LOADING

            val result = walkableRepository.updateWalks(walk, requireNotNull(UserManager.user))

            when(result){
                is Result.Success->{
                    _error.value = null
                    _loadStatus.value = LoadStatus.DONE
                    navigateToRating(walk)
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _loadStatus.value = LoadStatus.ERROR
                    navigateToRating(walk)
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _loadStatus.value = LoadStatus.ERROR
                    navigateToRating(walk)
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _loadStatus.value = LoadStatus.ERROR
                }
            }

        }
    }

    private fun startTimer(){
        runnable = Runnable{
            walkerTimer.value = walkerTimer.value?.plus(1)
            Log.d("JJ","timer ${walkerTimer.value}")
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    fun clientCurrentLocation(){

        _loadStatus.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = walkableRepository.getUserCurrentLocation()

            currentLocation.value = when(result){
                is Result.Success->{
                    _error.value = null
                    _loadStatus.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _loadStatus.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _loadStatus.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _loadStatus.value = LoadStatus.ERROR
                    null
                }
            }
            if(walkerStatus.value != WalkerStatus.PAUSING && result is Result.Success){
                startLocation.value = currentLocation.value
            }
            if(walkerStatus.value == WalkerStatus.WALKING){
                startRecordingDistance()
            }
        }

    }

    private fun createLocationRequest(): LocationRequest{
        return LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }



    fun drawPath(origin: LatLng, destination: LatLng, waypoints: List<LatLng>){
        coroutineScope.launch {

            _loadStatus.value = LoadStatus.LOADING

            val result = walkableRepository.drawPath(origin, destination, waypoints)

            _mapRoute.value = when(result){
                is Result.Success ->{
                    _error.value = null
                    _loadStatus.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _loadStatus.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _loadStatus.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _loadStatus.value = LoadStatus.ERROR
                    null
                }
            }
        }
    }

    private fun checkCameraHardWare(): Boolean{
        return WalkableApp.instance.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }



}
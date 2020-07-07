package tw.com.walkablecity.home

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getColor
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toDistance

class HomeViewModel(val walkableRepository: WalkableRepository, val argument: Route?): ViewModel(){

    companion object{
        const val UPDATE_INTERVAL_IN_MILLISECONDS = 5000L
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    private val fusedLocationClient = FusedLocationProviderClient(WalkableApp.instance)

    private val _checkPermission = MutableLiveData<Boolean>(false)
    val checkPermission: LiveData<Boolean> get() = _checkPermission

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
    private lateinit var locationCallback: LocationCallback

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    init{

        _walkerStatus.value = WalkerStatus.PREPARE
        clientCurrentLocation()
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
    }

    fun startStopSwitch(){
        when(walkerStatus.value){
            WalkerStatus.PREPARE -> checkBeforeWalking()
            WalkerStatus.PAUSING -> stopWalking()
            WalkerStatus.WALKING -> stopWalking()
            WalkerStatus.FINISH ->{}
        }
    }

    fun pauseResumeSwitch(){
        when(walkerStatus.value){
            WalkerStatus.PREPARE -> {}
            WalkerStatus.PAUSING -> resumeWalking()
            WalkerStatus.WALKING -> pauseWalking()
            WalkerStatus.FINISH ->{}
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
            waypoints = trackPoints.value as List<LatLng>)
        navigateToRating(walk)
    }

    private fun startTimer(){
        runnable = Runnable{
            walkerTimer.value = walkerTimer.value?.plus(1)
            Log.d("JJ","timer ${walkerTimer.value}")
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun clientCurrentLocation(){

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


    private fun checkBeforeWalking(){
        _checkPermission.value = true
    }

    fun checkPermissionComplete(){
        _checkPermission.value = false
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


}
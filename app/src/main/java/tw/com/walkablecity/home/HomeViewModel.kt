package tw.com.walkablecity.home



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.*
import tw.com.walkablecity.*
import tw.com.walkablecity.util.Util.getAccumulatedFromSharedPreference
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.directionresult.DirectionResult
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toDistance
import tw.com.walkablecity.ext.toGeoPoint
import tw.com.walkablecity.ext.toLatLng
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util.trackerPoints
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
    val walkableRepository: WalkableRepository,
    val argument: Route?,
    val destination: LatLng?
) : ViewModel() {

    private val _upgrade = MutableLiveData<Int>()
    val upgrade: LiveData<Int>
        get() = _upgrade

    private val _permissionDenied = MutableLiveData<Boolean>(false)
    val permissionDenied: LiveData<Boolean>
        get() = _permissionDenied

    private val _dontAskAgain = MutableLiveData<Boolean>(false)
    val dontAskAgain: LiveData<Boolean>
        get() = _dontAskAgain

    private val _navigateToLoad = MutableLiveData<Boolean>()
    val navigateToLoad: LiveData<Boolean>
        get() = _navigateToLoad

    private val _navigateToSearch = MutableLiveData<Boolean>()
    val navigateToSearch: LiveData<Boolean>
        get() = _navigateToSearch

    private val _navigateToRating = MutableLiveData<Walk>()
    val navigateToRating: LiveData<Walk>
        get() = _navigateToRating

    private val _walkerStatus = MutableLiveData<WalkerStatus>()
    val walkerStatus: LiveData<WalkerStatus>
        get() = _walkerStatus

    private val _loadStatus = MutableLiveData<LoadStatus>()
    val loadStatus: LiveData<LoadStatus>
        get() = _loadStatus

    private val _mapRoute = MutableLiveData<DirectionResult>()
    val mapRoute: LiveData<DirectionResult>
        get() = _mapRoute

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _photoPoints = MutableLiveData<MutableList<PhotoPoint>>(mutableListOf())
    val photoPoints: LiveData<MutableList<PhotoPoint>>
        get() = _photoPoints

    private val startTime = MutableLiveData<Timestamp>()
    private val endTime = MutableLiveData<Timestamp>()

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng>
        get() = _currentLocation

    private val _startLocation = MutableLiveData<LatLng>()
    val startLocation: LiveData<LatLng>
        get() = _startLocation

    private val _trackPoints = MutableLiveData<List<LatLng>>(mutableListOf())
    val trackPoints: LiveData<List<LatLng>>
        get() = _trackPoints

    val walkerDistance = Transformations.map(trackPoints) { list ->
        if (list == null || list.isEmpty()) {
            0F
        } else {
            list.toDistance()
        }
    }

    private val _walkerTimer = MutableLiveData<Long>(0L)
    val walkerTimer: LiveData<Long>
        get() = _walkerTimer

    val route = MutableLiveData<Route>().apply {
        value = argument
    }

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        if (walkerStatus.value == WalkerStatus.PAUSING || walkerStatus.value == WalkerStatus.WALKING) {
            _walkerStatus.value = WalkerStatus.FINISH
            pauseWalking()
        }
    }

    init {
        val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN)
            .format(now().seconds.times(1000)).toLong()

        Logger.d("date $date")

        _walkerStatus.value = WalkerStatus.PREPARE

        UserManager.user?.let { user ->
            newAccuBadgeCheck(user)
        }
    }

    fun addStartTrackPoint(latLng: LatLng) {
        trackerPoints.value = mutableListOf(latLng)
    }

    fun permissionDeniedForever() {
        _dontAskAgain.value = true
    }

    fun permissionDenied() {
        _permissionDenied.value = true
    }

    fun permissionGranted() {
        _permissionDenied.value = false
    }

    fun navigateToLoad() {
        _navigateToLoad.value = true
    }

    fun navigateToLoadComplete() {
        _navigateToLoad.value = false
    }

    fun navigateToSearch() {
        _navigateToSearch.value = true
    }

    fun navigateToSearchComplete() {
        _navigateToSearch.value = false
    }

    private fun navigateToRating(walk: Walk?) {
        _navigateToRating.value = walk
    }

    fun navigateToRatingComplete() {
        _navigateToRating.value = null
        _walkerStatus.value = WalkerStatus.PREPARE

    }

    fun addPhotoPoint(url: String) {
        val photoPoint = PhotoPoint(
            point =
            if (trackPoints.value.isNullOrEmpty()) requireNotNull(currentLocation.value).toGeoPoint()
            else trackPoints.value?.last()?.toGeoPoint(),
            photo = url
        )
        _photoPoints.value = photoPoints.value?.plus(photoPoint) as MutableList<PhotoPoint>
    }

    fun startStopSwitch() {
        when (walkerStatus.value) {
            WalkerStatus.PREPARE -> startWalking()
            WalkerStatus.PAUSING -> stopWalking()
            WalkerStatus.WALKING -> stopWalking()
            WalkerStatus.FINISH -> {
            }
        }
    }

    fun pauseResumeSwitch() {
        when (walkerStatus.value) {
            WalkerStatus.PREPARE -> {
            }
            WalkerStatus.PAUSING -> resumeWalking()
            WalkerStatus.WALKING -> pauseWalking()
            WalkerStatus.FINISH -> {
            }
        }
    }

    fun setTrackerTimer(time: Long){
        _walkerTimer.value = time
    }

    fun setTrackerPoints(list: List<LatLng>){
        _trackPoints.value = list
    }

    private fun startWalking() {
        _walkerStatus.value = WalkerStatus.WALKING

        //GPS recording
        clientCurrentLocation()
        startTime.value = now()

    }

    private fun pauseWalking() {
        _walkerStatus.value = WalkerStatus.PAUSING
    }

    private fun resumeWalking() {

        clientCurrentLocation()
        _walkerStatus.value = WalkerStatus.WALKING
    }

    private fun stopWalking() {
        _walkerStatus.value = WalkerStatus.FINISH

        endTime.value = now()

        //navigate to rating
        val walk = Walk(
            distance = walkerDistance.value as Float,
            duration = walkerTimer.value as Long,
            startTime = startTime.value as Timestamp,
            endTime = endTime.value as Timestamp,
            routeId = route.value?.id,
            waypoints = trackPoints.value?.map { it.toGeoPoint() } as List<GeoPoint>)
        updateWalk(walk)
    }

    private fun updateWalk(walk: Walk) {

        coroutineScope.launch {

            _loadStatus.value = LoadStatus.LOADING

            walkableRepository.updateWalks(walk, requireNotNull(UserManager.user)).apply {
                handleResultWith(_error, _loadStatus)
                if (this is Result.Success || this is Result.Fail) navigateToRating(walk)
            }
        }
    }

    fun clientCurrentLocation() {

        _loadStatus.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = walkableRepository.getUserCurrentLocation()

            _currentLocation.value = result.handleResultWith(_error, _loadStatus)?.toLatLng()

            if (walkerStatus.value != WalkerStatus.PAUSING && result is Result.Success) {
                _startLocation.value = currentLocation.value
            }
        }
    }

    fun drawPath(origin: LatLng, destination: LatLng, waypoints: List<LatLng>) {
        coroutineScope.launch {

            _loadStatus.value = LoadStatus.LOADING

            walkableRepository.drawPath(origin, destination, waypoints).apply {
                _mapRoute.value = handleResultWith(_error, _loadStatus)
            }
        }
    }

    private fun newAccuBadgeCheck(user: User) {
        val userKm = user.accumulatedKm?.total ?: 0f
        val accuKm = getAccumulatedFromSharedPreference(BadgeType.ACCU_KM.key, userKm)
        val upgradeKm = BadgeType.ACCU_KM.newAccuBadgeCheck(userKm, accuKm)


        val userHour = user.accumulatedHour?.total ?: 0f
        val accuHour = getAccumulatedFromSharedPreference(BadgeType.ACCU_HOUR.key, userHour)

        val upgradeHour = BadgeType.ACCU_HOUR.newAccuBadgeCheck(userHour, accuHour)
        Logger.d("userHour $userHour accuHour $accuHour upgradeHour $upgradeHour")

        _upgrade.value = upgradeKm + upgradeHour

    }
}
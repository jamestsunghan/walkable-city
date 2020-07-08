package tw.com.walkablecity.rating.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toComment
import tw.com.walkablecity.ext.toGeoPoint
import tw.com.walkablecity.rating.RatingType
import tw.com.walkablecity.userId
import java.text.SimpleDateFormat

class RatingItemViewModel(val walkableRepository: WalkableRepository, val selectedRoute: Route?
                          , val walk: Walk, val type: RatingType?) : ViewModel() {
    val colorId = R.color.primaryColor

    val duration = MutableLiveData<Float>().apply{
        value = walk.duration.toFloat() / 60
    }

    private val _sendRating = MutableLiveData<Boolean>(false)
    val sendRating: LiveData<Boolean>
        get() = _sendRating

    val ratingCoverage = MutableLiveData<Int>().apply{
        value = selectedRoute?.ratingAvr?.coverage?.toInt() ?: 4
    }
    val ratingTranquility = MutableLiveData<Int>().apply{
        value = selectedRoute?.ratingAvr?.tranquility?.toInt() ?: 3
    }
    val ratingScenery = MutableLiveData<Int>().apply{
        value = selectedRoute?.ratingAvr?.scenery?.toInt() ?: 4
    }
    val ratingRest = MutableLiveData<Int>().apply{
        value = selectedRoute?.ratingAvr?.rest?.toInt() ?: 3
    }
    val ratingSnack = MutableLiveData<Int>().apply{
        value = selectedRoute?.ratingAvr?.snack?.toInt() ?: 4
    }
    val ratingVibe = MutableLiveData<Int>().apply{
        value = selectedRoute?.ratingAvr?.vibe?.toInt() ?: 3
    }

    val routeCommentContent = MutableLiveData<String>()

    val routeTitle = MutableLiveData<String>()

    val routeDescription = MutableLiveData<String>()

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{

        when(type){
            RatingType.WALK->_sendRating.value = !type.willComment

            RatingType.ROUTE -> _sendRating.value = selectedRoute == null

        }

    }

    fun sendRouteRating(){
        val ratingUpdate = RouteRating(
            tranquility = ratingTranquility.value?.toFloat() as Float,
            vibe = ratingVibe.value?.toFloat() as Float,
            scenery = ratingScenery.value?.toFloat() as Float,
            snack = ratingSnack.value?.toFloat() as Float,
            coverage = ratingCoverage.value?.toFloat() as Float,
            rest = ratingRest.value?.toFloat() as Float
        )

        when(type){
            RatingType.ROUTE ->updateRouteRating(ratingUpdate)
            RatingType.WALK -> {
                if(routeTitle.value == null || routeDescription.value == null || routeCommentContent.value == null){
                    makeShortToast(R.string.not_complete_yet)
                }else{
                    createRoute(requireNotNull(routeTitle.value)
                        , requireNotNull(routeDescription.value), walk,ratingUpdate, userId, requireNotNull(routeCommentContent.value))
                }
            }
            else ->{}
        }

    }

    private fun updateRouteRating(ratingUpdate: RouteRating){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING
            _sendRating.value = when(val result = walkableRepository.updateRouteRating(ratingUpdate, selectedRoute as Route, userId)){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    true
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    false
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    false
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    false
                }
            }
        }

    }

    private fun createRoute(title: String, description: String, walk: Walk, rating: RouteRating, userId: Int, commentContent: String){
        val date = SimpleDateFormat("yyyyMMddHHmmss").format(walk.startTime.seconds.times(1000)).toLong()
        val waypoints = walk.waypoints.filterIndexed { index, latLng ->
            when {
                walk.waypoints.size<=10 -> index == index
                walk.waypoints.size<=18 -> index % 2 ==1
                else -> {
                    val factor = walk.waypoints.size / 9
                    index % factor ==1
                }
            }
        }
        val route = Route(
            id = date.times(100) + userId,
            title = title,
            description = description,
            length = walk.distance,
            minutes = walk.duration.toFloat().div(60),
            ratingAvr = rating,
            waypoints = waypoints.map{ it.toGeoPoint()},
            walkers = listOf(userId),
            mapImage = "http://thisisanimage.com",
            comments = listOf(commentContent.toComment(4,userId))
        )
        coroutineScope.launch {
            _status.value = LoadStatus.LOADING
            _sendRating.value = when(val result = walkableRepository.createRouteByUser(route)){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    true
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    false
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    false
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    false
                }
            }

        }

    }


}

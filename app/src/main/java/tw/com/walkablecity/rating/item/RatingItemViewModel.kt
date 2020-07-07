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
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.rating.RatingType
import tw.com.walkablecity.userId

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

    val routeComment = MutableLiveData<Comment>()

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
        if(selectedRoute == null){
            when(type){
                RatingType.WALK->{}
                RatingType.ROUTE -> _sendRating.value = true

            }
        }
    }

    fun sendRouteRating(){

        when(type){
            RatingType.ROUTE ->updateRouteRating()
            RatingType.WALK -> createRoute()
            else ->{}
        }

    }

    private fun updateRouteRating(){
        val ratingUpdate = RouteRating(
            tranquility = ratingTranquility.value?.toFloat() as Float,
            vibe = ratingVibe.value?.toFloat() as Float,
            scenery = ratingScenery.value?.toFloat() as Float,
            snack = ratingSnack.value?.toFloat() as Float,
            coverage = ratingCoverage.value?.toFloat() as Float,
            rest = ratingRest.value?.toFloat() as Float
        )
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

    fun createRoute(){
        _sendRating.value = true
    }


}

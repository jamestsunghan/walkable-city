package tw.com.walkablecity.rating.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.R
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.rating.RatingType

class RatingItemViewModel(val walkableRepository: WalkableRepository, val selectedRoute: Route?
                          , val walk: Walk, val type: RatingType?) : ViewModel() {
    val colorId = R.color.primaryColor

    val duration = MutableLiveData<Float>().apply{
        value = walk.duration.toFloat() / 60
    }

    private val _routeRating = MutableLiveData<Route>()
    val routeRating: LiveData<Route>
        get() = _routeRating

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


    fun sendRouteRating(){
        _routeRating.value = Route()
    }


}

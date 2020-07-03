package tw.com.walkablecity.rating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.R
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.source.WalkableRepository

class RatingViewModel(val walkableRepository: WalkableRepository) : ViewModel() {
    val colorId = R.color.primaryColor

    private val _routeRating = MutableLiveData<Route>()
    val routeRating: LiveData<Route>
        get() = _routeRating

    fun sendRouteRating(){
        _routeRating.value = Route()
    }

    fun sendComplete(){
        _routeRating.value = null
    }
}

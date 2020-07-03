package tw.com.walkablecity.loadroute.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteRating
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.data.source.WalkableRepository

class RouteItemViewModel( private val walkableRepository: WalkableRepository) : ViewModel() {

    private val _routeList = MutableLiveData<List<Route>>()
    val routeList: LiveData<List<Route>>
        get() = _routeList

    private val _filter = MutableLiveData<RouteSorting>()
    val filter :LiveData<RouteSorting> get() = _filter

    val selectRoute = MutableLiveData<Route>()


    init{
        _routeList.value = listOf(Route(title="第一條路", length = 1.23F, minutes = 28F
        ,ratingAvr = RouteRating(3.5F,4F,4.5F,2.5F,4F,3F)))
    }

    fun navigationComplete(){
        selectRoute.value = null
        _filter.value = null
    }

    fun filterSort(route: RouteSorting){
        _filter.value = route
    }

}

package tw.com.walkablecity.loadroute.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.source.WalkableRepository

class RouteItemViewModel( private val walkableRepository: WalkableRepository) : ViewModel() {

    private val _routeList = MutableLiveData<List<Route>>()
    val routeList: LiveData<List<Route>>
        get() = _routeList

    init{
        _routeList.value = listOf(Route(title="第一條路"))
    }
}

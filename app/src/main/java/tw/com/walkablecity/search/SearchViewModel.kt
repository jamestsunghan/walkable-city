package tw.com.walkablecity.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteRating
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.data.source.WalkableRepository

class SearchViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _filter = MutableLiveData<RouteSorting>()
    val filter : LiveData<RouteSorting> get() = _filter

    private val _selectedRoute = MutableLiveData<Route>()
    val selectedRoute : LiveData<Route> get() = _selectedRoute

    val range = MutableLiveData<List<Float>>(listOf(10F,25F))

    fun sortBy(sorting: RouteSorting){
        _filter.value = sorting
    }

    fun searchRoute(){
        val selectedFilter = filter.value
        if(selectedFilter == null){
            Util.makeShortToast(R.string.no_filter_yet)
        }else{
            matchRoute(selectedFilter)
        }
    }

    private fun matchRoute(filter: RouteSorting){
        _selectedRoute.value =
            Route(title="第一條路", length = 1.23F, minutes = 28F
                ,ratingAvr = RouteRating(3.5F,4F,4.5F,2.5F,4F,3F)
            )

    }

    fun searchComplete(){
        _selectedRoute.value = null
    }
}

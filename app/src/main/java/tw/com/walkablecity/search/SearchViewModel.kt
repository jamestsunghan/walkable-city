package tw.com.walkablecity.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toLocation

class SearchViewModel(val walkableRepository: WalkableRepository, val currentLocation: LatLng) :
    ViewModel() {

    private val _filter = MutableLiveData<RouteSorting>()
    val filter: LiveData<RouteSorting>
        get() = _filter

    private val _selectedRoute = MutableLiveData<Route>()
    val selectedRoute: LiveData<Route>
        get() = _selectedRoute

    private val _destination = MutableLiveData<LatLng>()
    val destination: LiveData<LatLng>
        get() = _destination

    private val _shortestTime = MutableLiveData<Int>(null)
    val shortestTime: LiveData<Int>
        get() = _shortestTime

    val shortestTimeText = Transformations.map(shortestTime) {
        if (it == null) {
            getString(R.string.no_direction_yet)
        } else {
            String.format(getString(R.string.about_minute), it.toFloat().div(60))
        }
    }

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun setDestination(destination: LatLng?) {
        _destination.value = destination
    }

    fun sortBy(sorting: RouteSorting) {
        _filter.value = sorting
    }


    fun searchRoute() {

        val selectedFilter = filter.value

        val selectedDestination = destination.value

        if (selectedFilter == null) {
            Util.makeShortToast(R.string.no_filter_yet)
        } else if (selectedDestination == null) {
            Util.makeShortToast(R.string.no_destination_yet)
        } else {
            matchRoute(selectedFilter, selectedDestination)
        }
    }

    fun getShortestTime(currentLocation: LatLng, destination: LatLng) {

        _status.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = walkableRepository.drawPath(currentLocation, destination, listOf())

            val paths = result.handleResultWith(_error, _status)?.routes

            _shortestTime.value = if (paths.isNullOrEmpty()) {
                null
            } else {
                paths[0].legs.map { it.duration?.value }.sumBy { it ?: 0 }
            }
        }
    }

    private fun matchRoute(filter: RouteSorting, destination: LatLng) {

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getAllRoute()

            val theOne = result.handleResultWith(_error, _status)

            _selectedRoute.value = if (theOne.isNullOrEmpty()) {
                Route()
            } else {
                theOne.filter {
                    it.waypoints.find { point ->
                        point.toLocation().distanceTo(destination.toLocation()) < NEARBY_DISTANCE ||
                                point.toLocation().distanceTo(currentLocation.toLocation()) < NEARBY_DISTANCE
                    } != null

                }.sortedByDescending {
                    it.ratingAvr?.sortingBy(filter)
                }[0]
            }
        }

    }

    fun searchComplete() {
        _selectedRoute.value = null
    }

    companion object {
        private const val NEARBY_DISTANCE = 500
    }
}

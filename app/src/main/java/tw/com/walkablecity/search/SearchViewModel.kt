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
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toLocation

class SearchViewModel(val walkableRepository: WalkableRepository, val currentLocation: LatLng) : ViewModel() {

    private val _filter = MutableLiveData<RouteSorting>()
    val filter : LiveData<RouteSorting> get() = _filter

    private val _selectedRoute = MutableLiveData<Route>()
    val selectedRoute : LiveData<Route> get() = _selectedRoute

    val range = MutableLiveData<List<Float>>(listOf(10F,25F))

    private val _destination = MutableLiveData<LatLng>()
    val destination: LiveData<LatLng> get() = _destination

    private val _shortestTime = MutableLiveData<Int>(null)
    val shortestTime: LiveData<Int> get() = _shortestTime


    val shortestTimeText = Transformations.map(shortestTime){
        if(it == null){
            getString(R.string.no_direction_yet)
        }else{
            String.format(getString(R.string.about_minute), it.toFloat().div(60))
        }
    }

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    init{
//        getCurrentLocation()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun setDestination(destination: LatLng?){
        _destination.value = destination
    }

    fun sortBy(sorting: RouteSorting){
        _filter.value = sorting
    }

//    fun getCurrentLocation(){
//        _status.value = LoadStatus.LOADING
//
//        coroutineScope.launch {
//            val result = walkableRepository.getUserCurrentLocation()
//
//            _location.value =
//
//        }
//    }

    fun searchRoute(){
        val selectedFilter = filter.value
        val selectedDestination = destination.value
        if(selectedFilter == null){
            Util.makeShortToast(R.string.no_filter_yet)
        }else if(selectedDestination == null){
            Util.makeShortToast(R.string.no_destination_yet)
        }else{
            matchRoute(selectedFilter, selectedDestination)
        }
    }

    fun getShortestTime(currentLocation: LatLng, destination: LatLng){
        _status.value = LoadStatus.LOADING
        coroutineScope.launch {
            val result = walkableRepository.drawPath(currentLocation, destination, listOf())

            _shortestTime.value = when(result){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data.routes[0].legs.map{it.duration?.value}.sumBy{ it ?: 0}
                }
                is Result.Fail ->{
                    _error.value = null
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = null
                    _status.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = null
                    _status.value = LoadStatus.ERROR
                    null
                }
            }
        }

    }

    private fun matchRoute(filter: RouteSorting, destination: LatLng){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getAllRoute()

            _selectedRoute.value = when(result){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    val theOne = result.data.filter{
                        it.waypoints.find {point->
                            point.toLocation().distanceTo(destination.toLocation()) <500 ||
                            point.toLocation().distanceTo(currentLocation.toLocation()) <500
                        }!= null

                    }.sortedByDescending {
                        when(filter){
                            RouteSorting.SCENERY    -> it.ratingAvr?.scenery
                            RouteSorting.SNACK      -> it.ratingAvr?.snack
                            RouteSorting.REST       -> it.ratingAvr?.rest
                            RouteSorting.TRANQUILITY-> it.ratingAvr?.tranquility
                            RouteSorting.COVERAGE   -> it.ratingAvr?.coverage
                            RouteSorting.VIBE       -> it.ratingAvr?.vibe
                        }
                    }
                    if(theOne.isNullOrEmpty()) Route()
                    else theOne[0]
                }

                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }




            }
        }

    }

    fun searchComplete(){
        _selectedRoute.value = null
    }
}

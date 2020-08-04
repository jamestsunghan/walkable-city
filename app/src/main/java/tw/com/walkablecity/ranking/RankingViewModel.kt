package tw.com.walkablecity.ranking


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.timeFilter

class RankingViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _routeAllList = MutableLiveData<List<Route>>()
    val routeAllList: LiveData<List<Route>>
        get() = _routeAllList

    private val _routeList = MutableLiveData<List<Route>>()
    val routeList: LiveData<List<Route>>
        get() = _routeList

    private val _filter = MutableLiveData<RouteSorting>()
    val filter: LiveData<RouteSorting>
        get() = _filter

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    val selectRoute = MutableLiveData<Route>()

    private val _routeTime = MutableLiveData<List<Float>>()
    val routeTime: LiveData<List<Float>>
        get() = _routeTime

    private val _sliderMax = MutableLiveData<Float>()
    val sliderMax: LiveData<Float>
        get() = _sliderMax

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        getRoutes()
    }

    fun navigationComplete() {
        selectRoute.value = null
        _filter.value = null
    }

    fun filterSort(sorting: RouteSorting) {
        _filter.value = sorting
    }

    private fun getRoutes() {
        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getAllRoute()

            _routeAllList.value = result.handleResultWith(_error, _status)
            _routeList.value = routeAllList.value

        }
    }

    fun setTimeFilter(range: List<Float>, max: Float) {
        _routeTime.value = range
        _sliderMax.value = max
    }

    fun timeFilter(list: List<Float>, max: Float, sorting: RouteSorting?) {
        _routeList.value = routeAllList.value?.timeFilter(list, max, sorting)
    }
}

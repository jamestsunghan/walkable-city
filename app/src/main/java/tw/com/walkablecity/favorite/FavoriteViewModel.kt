package tw.com.walkablecity.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ranking.RankingAdapter
import tw.com.walkablecity.userId

class FavoriteViewModel(val walkableRepository: WalkableRepository) : ViewModel() {


    private val _navigateToDetail = MutableLiveData<Route>()
    val navigateToDetail: LiveData<Route>
        get() = _navigateToDetail


    private val _routeAllList = MutableLiveData<List<Route>>()
    val routeAllList: LiveData<List<Route>>
        get() = _routeAllList

    private val _routeList = MutableLiveData<List<Route>>()
    val routeList: LiveData<List<Route>>
        get() = _routeList

    private val _filter = MutableLiveData<RouteSorting>()
    val filter : LiveData<RouteSorting> get() = _filter

    private val _status = MutableLiveData<LoadStatus>()
    val status : LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error : LiveData<String> get() = _error

    val selectRoute = MutableLiveData<Route>()

    private val _routeTime = MutableLiveData<List<Float>>()
    val routeTime: LiveData<List<Float>> get() = _routeTime

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{

        getFavoriteRoutes(userId)

    }

    fun navigateToDetail(route: Route){
        _navigateToDetail.value = route
    }


    fun navigateToDetailComplete(){
        _navigateToDetail.value = null
    }

    fun navigationComplete(){
        selectRoute.value = null
        _filter.value = null
    }

    fun filterSort(sorting: RouteSorting){
        _filter.value = sorting
    }

    private fun getFavoriteRoutes(userId: Int){
        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            when(val result = walkableRepository.getUserFavoriteRoutes(userId)){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE

                    _routeAllList.value = result.data
                    _routeList.value = routeAllList.value
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                }
                is Result.Error ->{
                    _error.value = WalkableApp.instance.getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                }

            }
        }
    }

    fun routeSorting(sorting: RouteSorting, adapter: FavoriteAdapter){
        _routeList.value = routeAllList.value?.sortedBy{

            when(sorting){
                RouteSorting.TRANQUILITY -> it.ratingAvr?.tranquility
                RouteSorting.SCENERY -> it.ratingAvr?.scenery
                RouteSorting.REST -> it.ratingAvr?.rest
                RouteSorting.SNACK -> it.ratingAvr?.snack
                RouteSorting.COVERAGE -> it.ratingAvr?.coverage
                RouteSorting.VIBE -> it.ratingAvr?.vibe

            }
        }?.reversed() ?: listOf()
        adapter.notifyDataSetChanged()
        Log.d("JJ","sortingList ${routeList.value?.map{it.title} ?: "null"}")
        Log.d("JJ","sorting tranquility ${routeList.value?.map{it.ratingAvr?.coverage} ?: "null"}")
    }

    fun setTimeFilter(range: List<Float>){
        _routeTime.value = range
    }

    fun timeFilter(list: List<Float>){
        _routeList.value = routeAllList.value?.filter{
            val range = list.sortedBy{it}
            range[0] < it.minutes && it.minutes < range[1]
        }
    }
}

package tw.com.walkablecity.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Result

class RankingPagingDataSource: PositionalDataSource<Route>() {

    private val _statusInitialLoad = MutableLiveData<LoadStatus>()

    val statusInitialLoad: LiveData<LoadStatus>
        get() = _statusInitialLoad

    private val _errorInitialLoad = MutableLiveData<String>()

    val errorInitialLoad: LiveData<String>
        get() = _errorInitialLoad

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)



    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Route>) {







    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Route>) {
        coroutineScope.launch {
        _statusInitialLoad.value = LoadStatus.LOADING

            val result = WalkableApp.instance.repo.getAllRoute()
            when (result) {
                is Result.Success -> {
                    _errorInitialLoad.value = null
                    _statusInitialLoad.value = LoadStatus.DONE
    //                    Logger.d("[${type.value}] loadInitial.result=${result.data.products}") // open it if you want to observe status
    //                    Logger.d("[${type.value}] loadInitial.paging=${result.data.paging}") // open it if you want to observe status
                    callback.onResult(result.data, 0, 20)
                }
                is Result.Fail -> {
                    _errorInitialLoad.value = result.error
                    _statusInitialLoad.value = LoadStatus.ERROR
                }
                is Result.Error -> {
                    _errorInitialLoad.value = result.exception.toString()
                    _statusInitialLoad.value = LoadStatus.ERROR
                }
                else -> {
                    _errorInitialLoad.value = getString(R.string.not_here)
                    _statusInitialLoad.value = LoadStatus.ERROR
                }
            }
        }
    }
}
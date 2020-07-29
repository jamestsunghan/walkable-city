package tw.com.walkablecity.profile.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.WalkerStatus

class ExploreViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _userWalks = MutableLiveData<List<Walk>>()
    val userWalks: LiveData<List<Walk>> get() = _userWalks

    val currentLocation = MutableLiveData<LatLng>()

    private val _permissionDenied =  MutableLiveData<Boolean>(false)
    val permissionDenied: LiveData<Boolean> get() = _permissionDenied

    private val _dontAskAgain = MutableLiveData<Boolean>(false)
    val dontAskAgain: LiveData<Boolean> get() = _dontAskAgain

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{
        UserManager.user?.id?.let{
            getUserWalks(it)
        }
    }

    fun permissionDeniedForever(){
        _dontAskAgain.value = true
    }

    fun permissionDenied(){
        _permissionDenied.value = true
    }

    fun permissionGranted(){
        _permissionDenied.value = false
    }

    fun getUserWalks(userId: String){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getUserWalks(userId)

            _userWalks.value = when(result){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
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
                else->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }
            }
        }
    }

    fun clientCurrentLocation(){

        _status.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = walkableRepository.getUserCurrentLocation()

            currentLocation.value = when(result){
                is Result.Success->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
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
}

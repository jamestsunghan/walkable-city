package tw.com.walkablecity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.WalkerStatus

class MainViewModel(val walkableRepository: WalkableRepository): ViewModel(){

    val currentFragment = MutableLiveData<CurrentFragmentType>()

    private val _walkerStatus = MutableLiveData<WalkerStatus>()
    val walkerStatus: LiveData<WalkerStatus> get() = _walkerStatus

    private val _invitation = MutableLiveData<Int>()
    val invitation: LiveData<Int> get() = _invitation

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getInvitation(userId: String){
        _status.value = LoadStatus.LOADING

        coroutineScope.launch {

            val result = walkableRepository.getUserInvitation(userId)

            _invitation.value = when(result){

                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data.size
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

    fun startWalking(){
        _walkerStatus.value = WalkerStatus.WALKING

        //timer start
        //GPS recording
    }

    fun pauseWalking(){
        _walkerStatus.value = WalkerStatus.PAUSING
        //timer pause
    }

    fun resumeWalking(){
        _walkerStatus.value = WalkerStatus.WALKING
        //timer resume
    }

    fun stopWalking(){
        _walkerStatus.value = WalkerStatus.FINISH

    }

}
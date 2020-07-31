package tw.com.walkablecity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.BadgeUpgrade
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.WalkerStatus

class MainViewModel(val walkableRepository: WalkableRepository): ViewModel(){

    val currentFragment = MutableLiveData<CurrentFragmentType>()

    private val _walkerStatus = MutableLiveData<WalkerStatus>()
    val walkerStatus: LiveData<WalkerStatus> get() = _walkerStatus

    private val _invitation = MutableLiveData<Int>()
    val invitation: LiveData<Int> get() = _invitation

    private val _friendCount = MutableLiveData<Int>()
    val friendCount: LiveData<Int> get() = _friendCount

    private val _eventCount = MutableLiveData<Int>()
    val eventCount: LiveData<Int> get() = _eventCount

    private val _badgeTotal = MutableLiveData<BadgeUpgrade>()
    val badgeTotal: LiveData<BadgeUpgrade> get() = _badgeTotal

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

    fun addToBadgeTotal(levelCount: Int, upgradeFrom: Int){
        val upgrade = badgeTotal.value ?: BadgeUpgrade()
        _badgeTotal.value =when(upgradeFrom){
            R.id.homeFragment -> upgrade.apply {
                home = levelCount
            }
            R.id.eventFragment -> upgrade.apply {
                event = levelCount
            }
            R.id.profileFragment -> upgrade.apply {
                friend = levelCount
            }
            else -> upgrade
        }

    }

    fun resetBadgeTotal(){
        _badgeTotal.value = BadgeUpgrade()
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

    fun getUserFriendCount(userId: String){
        _status.value = LoadStatus.LOADING

        coroutineScope.launch {

            val result = walkableRepository.getUserFriendSimple(userId)

            _friendCount.value = when(result){

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

    fun getUserEventCount(userId: String){
        _status.value = LoadStatus.LOADING

        coroutineScope.launch {

            val result = walkableRepository.getUserEvents(userId)

            _eventCount.value = when(result){

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
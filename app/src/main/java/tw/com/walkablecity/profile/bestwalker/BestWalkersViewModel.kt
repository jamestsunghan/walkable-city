package tw.com.walkablecity.profile.bestwalker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.FrequencyType
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository

class BestWalkersViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {


    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _userFriendList = MutableLiveData<List<User>>()
    val userFriendList: LiveData<List<User>> get() = _userFriendList

    private val _sortList = MutableLiveData<List<User>>()
    val sortList: LiveData<List<User>> get() = _sortList

    private val _frequencyType = MutableLiveData<FrequencyType>(FrequencyType.WEEKLY)
    val frequencyType: LiveData<FrequencyType> get() = _frequencyType

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{
        getUserFriends(requireNotNull(UserManager.user?.id))
    }

    fun sortList(list: List<User>){
        _sortList.value = (list as MutableList<User>).plus(requireNotNull(UserManager.user)).sortedByDescending { walker->
            when(frequencyType.value){
                FrequencyType.WEEKLY -> walker.accumulatedKm?.weekly
                FrequencyType.MONTHLY -> walker.accumulatedKm?.monthly
                else-> walker.accumulatedKm?.total
            }
        }
    }


    fun getUserFriends(userId: String){
        coroutineScope.launch {
            _status.value = LoadStatus.LOADING
            val result = walkableRepository.getUserFriends(userId)
            _userFriendList.value = when(result){

                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.ERROR
                    result.data
                }
                is Result.Fail    ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error   ->{
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

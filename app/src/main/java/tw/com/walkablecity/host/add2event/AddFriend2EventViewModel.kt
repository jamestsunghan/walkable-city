package tw.com.walkablecity.host.add2event

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
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.source.WalkableRepository

class AddFriend2EventViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> get() = _friendList

    val searchId = MutableLiveData<String>()


    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{
        UserManager.user?.id?.let{id->
            getUserFriends(id)
        }
    }

    fun getUserFriends(userId: String){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getUserFriendSimple(userId)

            _friendList.value = when(result){
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
                else ->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }
            }
        }
    }




}

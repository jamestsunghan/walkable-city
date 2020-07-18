package tw.com.walkablecity.host.add2event

import androidx.lifecycle.*
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

    val friendListed = MediatorLiveData<List<Friend>?>().apply {
        addSource(searchId){search ->
            value = when(search){
                null -> friendList.value
                else -> {
                    when(val friendList = friendList.value){
                        null -> friendList
                        else -> friendList.filter{friend->
                            friend.name!!.startsWith(search)
                        }
                    }
                }
            }
        }

        addSource(friendList){list->
            value = when(list){
                null -> list
                else -> {
                    when(val search = searchId.value){
                        null -> list
                        else -> list.filter{friend->
                            friend.name!!.startsWith(search)
                        }
                    }
                }
            }
        }
    }

    private val _addList = MutableLiveData<List<Friend>>()
    val addList: LiveData<List<Friend>> get() = _addList




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

    fun addFriendToAddList(friend: Friend){
        _addList.value = (addList.value ?: listOf()).plus(friend)
    }

    fun removeFriendToAddList(friend: Friend){
        _addList.value = (addList.value ?: listOf()).minus(friend)
    }

    fun friendSelected(){

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

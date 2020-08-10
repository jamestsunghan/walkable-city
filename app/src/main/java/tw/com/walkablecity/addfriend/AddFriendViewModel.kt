package tw.com.walkablecity.addfriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.util.UserAvatarOutlineProvider
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.util.Util.makeShortToast
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.source.WalkableRepository

class AddFriendViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _friendToAdd = MutableLiveData<Friend>()
    val friendToAdd: LiveData<Friend>
        get() = _friendToAdd

    private val _noSuchFriend = MutableLiveData<Boolean>()
    val noSuchFriend: LiveData<Boolean>
        get() = _noSuchFriend

    private val _alreadyFriend = MutableLiveData<Boolean>()
    val alreadyFriend: LiveData<Boolean>
        get() = _alreadyFriend

    private val _friendAdded = MutableLiveData<Boolean>()
    val friendAdded: LiveData<Boolean>
        get() = _friendAdded

    val idSearch = MutableLiveData<String>()

    val outlineProvider = UserAvatarOutlineProvider()

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun resetAddFriend(){
        _friendToAdd.value = null
        _friendAdded.value = null
    }

    fun searchFriendWithId(idCustom: String?){

        if (idCustom == null || idCustom.isEmpty()) {
            makeShortToast(R.string.no_search_id)
        } else {
            coroutineScope.launch {

                _status.value = LoadStatus.LOADING

                walkableRepository.searchFriendWithId(idCustom)
                    .handleResultWith(_error, _status).apply {
                    _friendToAdd.value = this?.toFriend()
                    _noSuchFriend.value = this == null
                }
            }
        }
    }

    fun checkFriendAdded(idCustom: String?){

        if (idCustom == null || idCustom.isEmpty()) {
            makeShortToast(R.string.no_search_id)
        } else {
            coroutineScope.launch {

                _status.value = LoadStatus.LOADING
                _noSuchFriend.value = null

                walkableRepository.checkFriendAdded(idCustom, requireNotNull(UserManager.user?.id)).apply{
                    _alreadyFriend.value = handleResultWith(_error, _status)
                }

            }
        }
    }

    fun addFriend(friend: Friend){
        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            walkableRepository.addFriend(friend, requireNotNull(UserManager.user)).apply{
                _friendAdded.value = handleResultWith(_error, _status)
            }
        }
    }


}

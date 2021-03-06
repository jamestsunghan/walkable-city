package tw.com.walkablecity.profile.bestwalker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository

class BestWalkersViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _userFriendList = MutableLiveData<List<User>>()
    val userFriendList: LiveData<List<User>>
        get() = _userFriendList

    private val _sortList = MutableLiveData<List<User>>()
    val sortList: LiveData<List<User>>
        get() = _sortList

    private val _accumulationType = MutableLiveData<AccumulationType>(AccumulationType.WEEKLY)
    val accumulationType: LiveData<AccumulationType>
        get() = _accumulationType

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        getUserFriends(requireNotNull(UserManager.user?.id))
    }

    fun sortList(list: List<User>, type: AccumulationType) {
        _sortList.value = list.plus(requireNotNull(UserManager.user))
            .sortedByDescending { walker ->
                when (type) {
                    AccumulationType.WEEKLY -> walker.accumulatedKm?.weekly
                    AccumulationType.MONTHLY -> walker.accumulatedKm?.monthly
                    else -> walker.accumulatedKm?.total
                }
            }
    }

    fun weekRanking() {
        _accumulationType.value = AccumulationType.WEEKLY
    }

    fun monthRanking() {
        _accumulationType.value = AccumulationType.MONTHLY
    }

    fun totalRanking() {
        _accumulationType.value = AccumulationType.TOTAL
    }


    private fun getUserFriends(userId: String) {

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val friendIds = walkableRepository.getUserFriendSimple(userId)
                .handleResultWith(_error, _status)?.map { requireNotNull(it.id) } ?: listOf()

            if (friendIds.isNotEmpty()) {
                walkableRepository.getFriendsById(friendIds).apply {
                    _userFriendList.value = handleResultWith(_error, _status)
                }
            } else {
                _userFriendList.value = listOf()
            }
        }
    }
}

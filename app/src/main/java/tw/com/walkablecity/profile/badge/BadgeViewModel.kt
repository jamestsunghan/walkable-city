package tw.com.walkablecity.profile.badge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Util.getAccumulatedFromSharedPreference
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.util.Util.getIntFromSP

class BadgeViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _friendCount = MutableLiveData<Int>()
    val friendCount: LiveData<Int>
        get() = _friendCount

    private val _eventCount = MutableLiveData<Int>()
    val eventCount: LiveData<Int>
        get() = _eventCount

    val accuHour = getAccumulatedFromSharedPreference(
        BadgeType.ACCU_HOUR.key
        , UserManager.user?.accumulatedHour?.total ?: 0f
    )

    val accuKm = getAccumulatedFromSharedPreference(
        BadgeType.ACCU_KM.key
        , UserManager.user?.accumulatedKm?.total ?: 0f
    )

    val sharedEventCount = getIntFromSP(BadgeType.EVENT_COUNT.key)

    val sharedFriendCount = getIntFromSP(BadgeType.FRIEND_COUNT.key)

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        UserManager.user?.id?.let { id ->
            getFriendCount(id)
            getEventCount(id)
        }

    }

    private fun getFriendCount(userId: String) {
        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getUserFriendSimple(userId)

            _friendCount.value = result.handleResultWith(_error, _status)?.size

        }
    }

    private fun getEventCount(userId: String) {
        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getAllEvents()

            _eventCount.value = result.handleResultWith(_error, _status)?.filter { event ->
                event.member.any { friend ->
                    friend.id == userId
                }
            }?.size

        }
    }
}

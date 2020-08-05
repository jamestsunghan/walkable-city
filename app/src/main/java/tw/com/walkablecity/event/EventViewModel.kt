package tw.com.walkablecity.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.data.source.WalkableRepository

class EventViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _navigateToHost = MutableLiveData<Boolean>(false)
    val navigateToHost: LiveData<Boolean>
        get() = _navigateToHost

    private val _showNoFriendDialog = MutableLiveData<Boolean>(false)
    val showNoFriendDialog: LiveData<Boolean>
        get() = _showNoFriendDialog

    private val _upgrade = MutableLiveData<Int>()
    val upgrade: LiveData<Int>
        get() = _upgrade


    fun navigateToHost(friendCount: Int) {
        if (friendCount > 0) {
            _navigateToHost.value = true
        } else {
            _showNoFriendDialog.value = true
        }
    }

    fun navigateToHostComplete() {
        _navigateToHost.value = false
    }

    fun setUpgrade(new: Int, old: Int) {
        Logger.d("new event $new old event $old")
        _upgrade.value = BadgeType.EVENT_COUNT.newCountBadgeCheck(new, old)
    }
}

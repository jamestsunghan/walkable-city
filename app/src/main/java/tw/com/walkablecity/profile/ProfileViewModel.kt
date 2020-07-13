package tw.com.walkablecity.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.source.WalkableRepository

class ProfileViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _navigateToAddFriend = MutableLiveData<Boolean>(false)
    val navigateToAddFriend: LiveData<Boolean> get() = _navigateToAddFriend

    private val _navigateToExplorer = MutableLiveData<Boolean>(false)
    val navigateToExplorer: LiveData<Boolean> get() = _navigateToExplorer

    private val _navigateToSetting = MutableLiveData<Boolean>(false)
    val navigateToSetting: LiveData<Boolean> get() = _navigateToSetting

    private val _navigateToWalkers = MutableLiveData<Boolean>(false)
    val navigateToWalkers: LiveData<Boolean> get() = _navigateToWalkers

    private val _navigateToBadge = MutableLiveData<Boolean>(false)
    val navigateToBadge: LiveData<Boolean> get() = _navigateToBadge

    fun addFriends(){
        _navigateToAddFriend.value = true
    }

    fun navigateToAddFriendComplete(){
        _navigateToAddFriend.value = false
    }

    fun navigateToSetting(){
        _navigateToSetting.value = true
    }

    fun navigateToSettingComplete(){
        _navigateToSetting.value = false
    }

    fun navigateToWalkers(){
        _navigateToWalkers.value = true
    }

    fun navigateToWalkersComplete(){
        _navigateToWalkers.value = false
    }

    fun navigateToBadge(){
        _navigateToBadge.value = true
    }

    fun navigateToBadgeComplete(){
        _navigateToBadge.value = false
    }

    fun navigateToExplorer(){
        _navigateToExplorer.value = true
    }

    fun navigateToExplorerComplete(){
        _navigateToExplorer.value = false
    }
}

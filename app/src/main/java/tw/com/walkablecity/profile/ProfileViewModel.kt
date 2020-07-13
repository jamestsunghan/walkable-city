package tw.com.walkablecity.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.source.WalkableRepository

class ProfileViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _navigateToAddFriend = MutableLiveData<Boolean>(false)
    val navigateToAddFriend: LiveData<Boolean> get() = _navigateToAddFriend

    fun addFriends(){
        _navigateToAddFriend.value = true
    }

    fun navigateToAddFriendComplete(){
        _navigateToAddFriend.value = false
    }
}

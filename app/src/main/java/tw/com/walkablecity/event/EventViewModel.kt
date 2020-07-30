package tw.com.walkablecity.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.data.source.WalkableRepository

class EventViewModel(val walkableRepository: WalkableRepository) : ViewModel() {


    private val _navigateToHost = MutableLiveData<Boolean>(false)
    val navigateToHost: LiveData<Boolean> get() = _navigateToHost

    private val _upgrade = MutableLiveData<Int>()
    val upgrade: LiveData<Int> get() = _upgrade


    fun navigateToHost(){
        _navigateToHost.value = true
    }

    fun navigateToHostComplete(){
        _navigateToHost.value = false
    }

    fun setUpgrade(new: Int, old: Int){
        _upgrade.value = BadgeType.EVENT_COUNT.newCountBadgeCheck(new, old)
    }
}

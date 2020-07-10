package tw.com.walkablecity.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.source.WalkableRepository

class EventViewModel(val walkableRepository: WalkableRepository) : ViewModel() {


    private val _navigateToHost = MutableLiveData<Boolean>(false)
    val navigateToHost: LiveData<Boolean> get() = _navigateToHost




    fun navigateToHost(){
        _navigateToHost.value = true
    }

    fun navigateToHostComplete(){
        _navigateToHost.value = false
    }
}

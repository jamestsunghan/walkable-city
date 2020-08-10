package tw.com.walkablecity.home.destroywalk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DestroyWalkDialogViewModel : ViewModel() {

    private val _destroyAll = MutableLiveData<Boolean>()
    val destroyAll: LiveData<Boolean> get() = _destroyAll


    fun willDestroyWalk(){
        _destroyAll.value = true
    }

    fun willNotDestroyWalk(){
        _destroyAll.value = false
    }

    fun navigateComplete(){
        _destroyAll.value = null
    }
}

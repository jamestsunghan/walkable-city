package tw.com.walkablecity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.WalkerStatus

class MainViewModel(walkableRepository: WalkableRepository): ViewModel(){

    val currentFragment = MutableLiveData<CurrentFragmentType>()

    private val _walkerStatus = MutableLiveData<WalkerStatus>()
    val walkerStatus: LiveData<WalkerStatus> get() = _walkerStatus

    fun startWalking(){
        _walkerStatus.value = WalkerStatus.WALKING

        //timer start
        //GPS recording
    }

    fun pauseWalking(){
        _walkerStatus.value = WalkerStatus.PAUSING
        //timer pause
    }

    fun resumeWalking(){
        _walkerStatus.value = WalkerStatus.WALKING
        //timer resume
    }

    fun stopWalking(){
        _walkerStatus.value = WalkerStatus.FINISH

    }

}
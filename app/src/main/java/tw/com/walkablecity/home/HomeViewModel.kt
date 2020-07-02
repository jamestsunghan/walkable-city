package tw.com.walkablecity.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.data.source.WalkableRepository

class HomeViewModel(val walkableRepository: WalkableRepository, val route: Route?): ViewModel(){
    private val _navigateToLoad = MutableLiveData<Boolean>()
    val navigateToLoad: LiveData<Boolean> get() = _navigateToLoad

    private val _navigateToSearch = MutableLiveData<Boolean>()
    val navigateToSearch: LiveData<Boolean> get() = _navigateToSearch

    private val _navigateToRating = MutableLiveData<Boolean>()
    val navigateToRating: LiveData<Boolean> get() = _navigateToRating

    private val _walkerStatus = MutableLiveData<WalkerStatus>()
    val walkerStatus: LiveData<WalkerStatus> get() = _walkerStatus

    init{

        _walkerStatus.value = WalkerStatus.PREPARE

    }

    fun navigateToLoad(){
        _navigateToLoad.value = true
    }
    fun navigateToLoadComplete(){
        _navigateToLoad.value = false
    }

    fun navigateToSearch(){
        _navigateToSearch.value = true
    }

    fun navigateToSearchComplete(){
        _navigateToSearch.value = false
    }

    fun navigateToRating(){
        _navigateToRating.value = true
    }

    fun navigateToRatingComplete(){
        _navigateToRating.value = false
    }

    fun startStopSwitch(){
        when(walkerStatus.value){
            WalkerStatus.PREPARE -> startWalking()
            WalkerStatus.PAUSING -> stopWalking()
            WalkerStatus.WALKING -> stopWalking()
            WalkerStatus.FINISH ->{}
        }
    }

    fun pauseResumeSwitch(){
        when(walkerStatus.value){
            WalkerStatus.PREPARE -> {}
            WalkerStatus.PAUSING -> resumeWalking()
            WalkerStatus.WALKING -> pauseWalking()
            WalkerStatus.FINISH ->{}
        }
    }
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

        //timer stop

        //GPS stop recording

        //navigate to rating
        navigateToRating()
    }

}
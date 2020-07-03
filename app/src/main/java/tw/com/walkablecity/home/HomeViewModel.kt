package tw.com.walkablecity.home

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.data.source.WalkableRepository

class HomeViewModel(val walkableRepository: WalkableRepository, val argument: Route?): ViewModel(){
    private val _navigateToLoad = MutableLiveData<Boolean>()
    val navigateToLoad: LiveData<Boolean> get() = _navigateToLoad

    private val _navigateToSearch = MutableLiveData<Boolean>()
    val navigateToSearch: LiveData<Boolean> get() = _navigateToSearch

    private val _navigateToRating = MutableLiveData<Boolean>()
    val navigateToRating: LiveData<Boolean> get() = _navigateToRating

    private val _walkerStatus = MutableLiveData<WalkerStatus>()
    val walkerStatus: LiveData<WalkerStatus> get() = _walkerStatus

    val walkerTimer = MutableLiveData<Long>(0L)

    val walkerDistance = MutableLiveData<Float>(0F)

    val startTime = MutableLiveData<Timestamp>()

    val duration = MutableLiveData<Long>()

    val pauseTime = MutableLiveData<Timestamp>()

    val endTime = MutableLiveData<Timestamp>()

    private var handler = Handler()
    private lateinit var runnable: Runnable

    val route = MutableLiveData<Route>().apply {
        value = argument
    }



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

        startTime.value = now()
//        pauseTime.value = now()
        //timer start
        startTimer()
        //GPS recording
    }

    fun pauseWalking(){
        _walkerStatus.value = WalkerStatus.PAUSING
        //timer pause
        handler.removeCallbacks(runnable)
    }

    fun resumeWalking(){
        _walkerStatus.value = WalkerStatus.WALKING
        //timer resume
        startTimer()
    }

    fun stopWalking(){
        _walkerStatus.value = WalkerStatus.FINISH

        //timer stop
        handler.removeCallbacks(runnable)
        endTime.value = now()
        //GPS stop recording

        //navigate to rating
        navigateToRating()
    }

    private fun startTimer(){
        runnable = Runnable{
            walkerTimer.value = walkerTimer.value?.plus(1)
            Log.d("JJ","timer ${walkerTimer.value}")
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

}
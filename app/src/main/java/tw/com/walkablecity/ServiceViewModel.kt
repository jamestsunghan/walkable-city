package tw.com.walkablecity

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util

class ServiceViewModel: ViewModel() {

    private val _walkerTimer = MutableLiveData<Long>(0L)
    val walkerTimer: LiveData<Long>
        get() = _walkerTimer

    val walkerTimerText = Transformations.map(walkerTimer){time->
        if(time == null){
            null
        } else {
            val minutes = time / 60
            val seconds = time % 60
            StringBuilder().append(Util.lessThenTenPadStart(minutes))
                .append(":").append(Util.lessThenTenPadStart(seconds)).toString()
        }
    }



    private var handler = Handler()
    private lateinit var runnable: Runnable


    fun startTimer() {
        runnable = Runnable {
            _walkerTimer.value = walkerTimer.value?.plus(1)
            Logger.d("timer ${walkerTimer.value}")
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    fun pauseWalking() {

        //timer pause
        handler.removeCallbacks(runnable)
    }

    fun resumeWalking() {
        //timer resume
        startTimer()
    }





}
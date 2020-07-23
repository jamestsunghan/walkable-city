package tw.com.walkablecity

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.work.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tw.com.walkablecity.data.source.DefaultWalkableRepository
import tw.com.walkablecity.data.source.WalkableRemoteDataSource
import tw.com.walkablecity.work.DailyWorker
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class WalkableApp: Application(){

    lateinit var repo: DefaultWalkableRepository

    val appScope = CoroutineScope(Dispatchers.Default)

    companion object {
        var instance: WalkableApp by Delegates.notNull()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
//            .apply {
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                    setRequiresDeviceIdle(true)
//                }
//            }
            .build()

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        delayInit()

        repo = DefaultWalkableRepository(WalkableRemoteDataSource)
    }

    private fun delayInit() = appScope.launch {
        setupUpdateAndResetWork()
    }

    private fun setupUpdateAndResetWork(){
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 10)
            set(Calendar.SECOND, 0)
        }

        if(dueDate.before(currentDate)){
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis


        val dailyRequest = OneTimeWorkRequestBuilder<DailyWorker>()
            .setConstraints(constraints)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(dailyRequest)

    }


}
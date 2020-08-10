package tw.com.walkablecity


import android.app.Application
import androidx.work.*
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tw.com.walkablecity.data.source.DefaultWalkableRepository
import tw.com.walkablecity.data.source.WalkableRemoteDataSource
import tw.com.walkablecity.util.Util.setDailyTimer
import tw.com.walkablecity.work.DailyWorker
import tw.com.walkablecity.work.MealWorker
import tw.com.walkablecity.work.WeatherWorker

import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class WalkableApp : Application() {

    lateinit var repo: DefaultWalkableRepository

    private val appScope = CoroutineScope(Dispatchers.Default)

    companion object {

        var instance: WalkableApp by Delegates.notNull()

        val constraintsWeather = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED).build()

        val constraintsMeal = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED).build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
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

    private fun setupUpdateAndResetWork() {

        val timeDiff = setDailyTimer(3, 0, 0)

        val dailyRequest = OneTimeWorkRequestBuilder<DailyWorker>()
            .setConstraints(constraints)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(dailyRequest)

    }

    fun getWeather(activate: Boolean) {

        val random = (0..119).random()

        val timeDiff = setDailyTimer(8 + random / 60, random % 60, random % 60)

        val weatherRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setConstraints(constraints)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()

        val workManager = WorkManager.getInstance(this)
        if (activate) {
            workManager.enqueue(weatherRequest)
        } else {
            workManager.cancelWorkById(weatherRequest.id)
        }
    }

    fun notifyAfterMeal(activate: Boolean) {

        val timeDiff = setDailyTimer(19, 30, 0)

        val mealRequest = OneTimeWorkRequestBuilder<MealWorker>()
            .setConstraints(constraintsMeal)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()

        val workManager = WorkManager.getInstance(this)
        if (activate) {
            workManager.enqueue(mealRequest)
        } else {
            workManager.cancelWorkById(mealRequest.id)
        }
    }
}
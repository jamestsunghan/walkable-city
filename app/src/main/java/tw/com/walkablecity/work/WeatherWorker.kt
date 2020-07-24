package tw.com.walkablecity.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.FrequencyType
import tw.com.walkablecity.data.User
import tw.com.walkablecity.profile.settings.SettingsFragment
import java.util.*
import java.util.concurrent.TimeUnit

class WeatherWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object{
        val repo = WalkableApp.instance.repo
    }

    override suspend fun doWork(): Result {
        return try{

            if(ContextCompat.checkSelfPermission(
                    WalkableApp.instance,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED){

                val currentDate = Calendar.getInstance()


                val dueDate = Calendar.getInstance().apply{
                    set(Calendar.HOUR_OF_DAY, 3)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                if(dueDate.before(currentDate)){
                    dueDate.add(Calendar.HOUR_OF_DAY, 24)
                }

                val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
                val dailyRequest = OneTimeWorkRequestBuilder<DailyWorker>()
                    .setConstraints(WalkableApp.constraints)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build()


                val location: LatLng? = when(val result = repo.getUserCurrentLocation()){
                    is tw.com.walkablecity.data.Result.Success ->{result.data}
                    is tw.com.walkablecity.data.Result.Fail ->{null}
                    is tw.com.walkablecity.data.Result.Error ->{null}
                    else -> null
                }

                val weather = when(val result = repo.getWeather(location ?:LatLng(0.0,0.0))){
                    is tw.com.walkablecity.data.Result.Success ->{result.data}
                    is tw.com.walkablecity.data.Result.Fail ->{null}
                    is tw.com.walkablecity.data.Result.Error ->{null}
                    else -> null
                }

                WorkManager.getInstance(applicationContext).enqueue(dailyRequest)

                Log.d("JJ_work","I'm here!")

                Result.success()

            }else{
                Result.failure()
            }

        }catch (e: HttpException){
            Result.retry()
        }
    }
}
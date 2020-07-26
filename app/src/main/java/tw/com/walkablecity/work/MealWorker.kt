package tw.com.walkablecity.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.User
import java.util.*
import java.util.concurrent.TimeUnit

class MealWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object{
        val repo = WalkableApp.instance.repo
        val user = Firebase.auth.currentUser
        const val CHANNEL_ID = "meal"
        const val NOTIFY_ID = 0x505
        const val ONE_DAY = 60 * 60 * 24
    }


    override suspend fun doWork(): Result {
        return try{
            if(user == null){
                Result.retry()
            }else{
                createNotificationChannel()
                val currentDate = Calendar.getInstance()

                val dueDate = Calendar.getInstance().apply{
                    set(Calendar.HOUR_OF_DAY, 19)
                    set(Calendar.MINUTE, 30)
                    set(Calendar.SECOND, 0)
                }

                if(dueDate.before(currentDate)){
                    dueDate.add(Calendar.HOUR_OF_DAY, 24)
                }

                val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
                val mealRequest = OneTimeWorkRequestBuilder<MealWorker>()
                    .setConstraints(WalkableApp.constraintsMeal)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build()

                val lastWalk = when(val result = repo.getUserLatestWalk(user.uid)){
                    is tw.com.walkablecity.data.Result.Success ->{result.data}
                    is tw.com.walkablecity.data.Result.Fail ->{null}
                    is tw.com.walkablecity.data.Result.Error ->{null}
                    else -> null
                }

                val contentText = if(lastWalk == null){
                    getString(R.string.first_walk)
                }else{
                    val timeGap = (now().seconds - requireNotNull(lastWalk.endTime?.seconds)).div(ONE_DAY).toInt()
                    String.format(getString(R.string.after_meal_talk), timeGap)
                }

                val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setContentTitle(getString(R.string.after_meal_walk))
                    .setSmallIcon(R.drawable.footprints)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))

                with(NotificationManagerCompat.from(applicationContext)){
                    notify(NOTIFY_ID, builder.build())
                }

                WorkManager.getInstance(applicationContext).enqueue(mealRequest)

                Log.d("JJ_work","I'm here!")




                Result.success()
            }


        }catch (e: HttpException){
            Result.retry()
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val name = Util.getString(R.string.walkable_city)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = Util.getString(R.string.after_meal)
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}
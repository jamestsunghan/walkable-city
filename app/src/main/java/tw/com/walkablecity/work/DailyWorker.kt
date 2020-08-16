package tw.com.walkablecity.work

import android.content.Context
import androidx.work.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.FrequencyType
import tw.com.walkablecity.data.User
import java.util.*
import java.util.concurrent.TimeUnit

class DailyWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        val repo = WalkableApp.instance.repo
        val user = Firebase.auth.currentUser
    }

    override suspend fun doWork(): Result {

        return try {
            if (user == null) {
                Result.retry()
            } else {
                val currentDate = Calendar.getInstance()
                val user: User? = when (val result = repo.getUser(user.uid)) {
                    is tw.com.walkablecity.data.Result.Success -> {
                        result.data
                    }
                    else -> null
                }

                val daily =
                    when (val result = repo.updateUserAccumulated(user, FrequencyType.DAILY)) {
                        is tw.com.walkablecity.data.Result.Success -> {
                            result.data
                        }
                        else -> false
                    }

                if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    val weekly = when (val result =
                        repo.updateUserAccumulated(user, FrequencyType.WEEKLY)) {
                        is tw.com.walkablecity.data.Result.Success -> {
                            result.data
                        }
                        else -> false
                    }
                }

                if (currentDate.get(Calendar.DAY_OF_MONTH) == 1) {
                    val monthly = when (val result =
                        repo.updateUserAccumulated(user, FrequencyType.MONTHLY)) {
                        is tw.com.walkablecity.data.Result.Success -> {
                            result.data
                        }
                        else -> false
                    }
                }

                val dueDate = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 3)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                if (dueDate.before(currentDate)) {
                    dueDate.add(Calendar.HOUR_OF_DAY, 24)
                }

                val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

                val dailyRequest = OneTimeWorkRequestBuilder<DailyWorker>()
                    .setConstraints(WalkableApp.constraints)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build()

                WorkManager.getInstance(applicationContext).enqueue(dailyRequest)

                Logger.d("JJ_work I'm here!")

                Result.success()
            }
        } catch (e: HttpException) {
            Result.retry()
        }
    }


}
package tw.com.walkablecity.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.FrequencyType
import tw.com.walkablecity.data.User
import java.util.*
import java.util.concurrent.TimeUnit

class DailyWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "dailyWorker"
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
                    is tw.com.walkablecity.data.Result.Fail -> {
                        null
                    }
                    is tw.com.walkablecity.data.Result.Error -> {
                        null
                    }
                    else -> null
                }
                val eventList = when (val result = repo.getNowAndFutureEvents()) {
                    is tw.com.walkablecity.data.Result.Success -> {
                        result.data
                            .filter { event ->
                                requireNotNull(event.startDate) < now()
                                        && (event.type == EventType.FREQUENCY)
                                        && event.member.find { it.id == UserManager.user?.id } != null
                            }
                    }
                    is tw.com.walkablecity.data.Result.Fail -> {
                        listOf()
                    }
                    is tw.com.walkablecity.data.Result.Error -> {
                        listOf()
                    }
                    else -> listOf()
                }

                val eventDaily =
                    eventList.filter { it.target?.frequencyType == FrequencyType.DAILY }
                val eventWeekly =
                    eventList.filter { it.target?.frequencyType == FrequencyType.WEEKLY }
                val eventMonthly =
                    eventList.filter { it.target?.frequencyType == FrequencyType.MONTHLY }

                val daily =
                    when (val result = repo.updateEvents(user, eventDaily, FrequencyType.DAILY)) {
                        is tw.com.walkablecity.data.Result.Success -> {
                            result.data
                        }
                        is tw.com.walkablecity.data.Result.Fail -> {
                            false
                        }
                        is tw.com.walkablecity.data.Result.Error -> {
                            false
                        }
                        else -> false
                    }

                if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    val weekly = when (val result =
                        repo.updateEvents(user, eventWeekly, FrequencyType.WEEKLY)) {
                        is tw.com.walkablecity.data.Result.Success -> {
                            result.data
                        }
                        is tw.com.walkablecity.data.Result.Fail -> {
                            false
                        }
                        is tw.com.walkablecity.data.Result.Error -> {
                            false
                        }
                        else -> false
                    }
                }

                if (currentDate.get(Calendar.DAY_OF_MONTH) == 1) {
                    val monthly = when (val result =
                        repo.updateEvents(user, eventMonthly, FrequencyType.MONTHLY)) {
                        is tw.com.walkablecity.data.Result.Success -> {
                            result.data
                        }
                        is tw.com.walkablecity.data.Result.Fail -> {
                            false
                        }
                        is tw.com.walkablecity.data.Result.Error -> {
                            false
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
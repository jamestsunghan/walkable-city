package tw.com.walkablecity.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.FrequencyType
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository
import java.util.*
import java.util.concurrent.TimeUnit

class DailyWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object{
        const val WORK_NAME = "dailyWorker"
        val repo = WalkableApp.instance.repo
        val user = Firebase.auth.currentUser
    }



    override suspend fun doWork(): Result {

        return try{
            if(user == null){
                Result.retry()
            }else{
                val currentDate = Calendar.getInstance()
                val user: User? = when(val result = repo.getUser(user.uid)){
                    is tw.com.walkablecity.data.Result.Success ->{result.data}
                    is tw.com.walkablecity.data.Result.Fail ->{null}
                    is tw.com.walkablecity.data.Result.Error ->{null}
                    else -> null
                }
                val eventList = when(val result = repo.getUserParticipateEvent(requireNotNull(user))){
                    is tw.com.walkablecity.data.Result.Success ->{result.data
                        .filter{ requireNotNull(it.startDate) < now() && (it.type == EventType.FREQUENCY)}}
                    is tw.com.walkablecity.data.Result.Fail ->{ listOf()}
                    is tw.com.walkablecity.data.Result.Error ->{ listOf()}
                    else ->  listOf()
                }

                val eventDaily = eventList.filter{it.target?.frequencyType == FrequencyType.DAILY}
                val eventWeekly = eventList.filter{it.target?.frequencyType == FrequencyType.WEEKLY}
                val eventMonthly = eventList.filter{it.target?.frequencyType == FrequencyType.MONTHLY}

                val daily =  when(val result = repo.updateDailyEvents(user, eventDaily)){
                    is tw.com.walkablecity.data.Result.Success ->{result.data }
                    is tw.com.walkablecity.data.Result.Fail ->{ false}
                    is tw.com.walkablecity.data.Result.Error ->{false}
                    else ->  false
                }

                if(currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
                    val weekly =  when(val result = repo.updateWeeklyEvents(user, eventWeekly)){
                        is tw.com.walkablecity.data.Result.Success ->{result.data }
                        is tw.com.walkablecity.data.Result.Fail ->{ false}
                        is tw.com.walkablecity.data.Result.Error ->{false}
                        else ->  false
                    }
                }

                if(currentDate.get(Calendar.DAY_OF_MONTH) == 23){
                    val monthly =  when(val result = repo.updateMonthlyEvents(user, eventMonthly)){
                        is tw.com.walkablecity.data.Result.Success ->{result.data }
                        is tw.com.walkablecity.data.Result.Fail ->{ false}
                        is tw.com.walkablecity.data.Result.Error ->{false}
                        else ->  false
                    }
                }

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

                WorkManager.getInstance(applicationContext).enqueue(dailyRequest)

                Log.d("JJ_work","I'm here!")

                Result.success()
            }
        }catch (e: HttpException){
            Result.retry()
        }
    }
}
package tw.com.walkablecity.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.maps.model.LatLng
import retrofit2.HttpException
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.util.Util.lessThenTenPadStart
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.ext.toLatLng
import tw.com.walkablecity.util.Util.setDailyTimer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class WeatherWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        createNotificationChannel()
        return try {

            if (ContextCompat.checkSelfPermission(
                    WalkableApp.instance,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val currentDate = Calendar.getInstance()
                val random = (0..119).random()

                val timeDiff = setDailyTimer(8 + (random / 60), random % 60, random % 60)
                val dailyRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
                    .setConstraints(WalkableApp.constraintsWeather)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build()


                val location: LatLng? = when (val result = repo.getUserCurrentLocation()) {
                    is tw.com.walkablecity.data.Result.Success -> {
                        result.data?.toLatLng()
                    }
                    is tw.com.walkablecity.data.Result.Fail -> {
                        null
                    }
                    is tw.com.walkablecity.data.Result.Error -> {
                        null
                    }
                    else -> null
                }

                val weather = when (val result = repo.getWeather(location ?: LatLng(0.0, 0.0))) {
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


                val hourWalkable = weather?.hourly?.filter { item ->
                    item.feelsLike ?: 50f < 35f && item.feelsLike ?: 0f > 15f && item.pop ?: 1f < 0.7
                }
                    ?.filter { item ->
                        val itemDate =
                            SimpleDateFormat("dd", Locale.TAIWAN).format(item.dt?.times(1000))
                        val todayDate =
                            lessThenTenPadStart(currentDate.get(Calendar.DAY_OF_MONTH).toLong())
                        Logger.d("JJ_weather hour date $itemDate & today date $todayDate")
                        itemDate == todayDate
                    }?.sortedBy {
                        abs((it.feelsLike ?: 0f).minus(25))
                    }
                val currentUvi = weather?.current?.uvi
                val text = StringBuilder()
                when {
                    currentUvi == null -> {
                        Logger.d("no uvi for today")
                    }
                    currentUvi > 10 -> {
                        text.append(
                            getString(R.string.today_uvi) + String.format(
                                getString(R.string.uvi_very_strong),
                                currentUvi
                            )
                        ).append("\n")
                    }
                    currentUvi > 7 -> {
                        text.append(
                            getString(R.string.today_uvi) + String.format(
                                getString(R.string.uvi_strong),
                                currentUvi
                            )
                        ).append("\n")
                    }
                    currentUvi > 5 -> {
                        text.append(
                            getString(R.string.today_uvi) + String.format(
                                getString(R.string.uvi_medium),
                                currentUvi
                            )
                        ).append("\n")
                    }
                    currentUvi > 3 -> {
                        text.append(
                            getString(R.string.today_uvi) + String.format(
                                getString(R.string.uvi_weak),
                                currentUvi
                            )
                        ).append("\n")
                    }
                    else -> {
                        text.append(
                            getString(R.string.today_uvi) + String.format(
                                getString(R.string.uvi_very_weak),
                                currentUvi
                            )
                        ).append("\n")
                    }
                }
                val contentText = if (hourWalkable.isNullOrEmpty()) {
                    if (weather?.hourly?.any {
                            it.feelsLike!! > 35f
                        } == true) {
                        StringBuilder().append(getString(R.string.good_weather_content_hot))

                        text.toString()
                    } else {
                        StringBuilder().append(getString(R.string.good_weather_content_cold))
                        text.toString()
                    }
                } else {
                    text.append(getString(R.string.good_weather_hour)).append("\n")
                    for (item in hourWalkable) {
                        val hrDisplay =
                            SimpleDateFormat("HH:mm", Locale.TAIWAN).format(item.dt?.times(1000))
                        text.append(hrDisplay).append(getString(R.string.feels_like))
                            .append(item.feelsLike)
                            .append(getString(R.string.rain_percentage)).append(
                                String.format(
                                    getString(R.string.accomplish_rate),
                                    item.pop?.times(100)
                                )
                            ).append("\n")
                        Logger.d("JJ_weather weather hour $hrDisplay feels like ${item.feelsLike} Celsius ")

                    }
                    Logger.d("today good weather ${text.toString()}")
                    text.toString()
                }


                val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.footprints)
                    .setContentTitle(getString(R.string.good_weather_hour))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))

                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(NOTIFY_ID, builder.build())
                }

                WorkManager.getInstance(applicationContext).enqueue(dailyRequest)

                Logger.d("JJ_work I'm here!")

                Result.success()

            } else {
                Result.failure()
            }

        } catch (e: HttpException) {
            Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = getString(R.string.walkable_city)

            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = getString(R.string.good_weather)
            }

            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        val repo = WalkableApp.instance.repo
        const val CHANNEL_ID = "walkable"
        const val NOTIFY_ID = 0x501
    }
}
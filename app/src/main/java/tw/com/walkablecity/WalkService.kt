package tw.com.walkablecity

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import tw.com.walkablecity.ext.toDistance
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util
import tw.com.walkablecity.util.Util.trackTimer
import tw.com.walkablecity.util.Util.trackerPoints
import tw.com.walkablecity.work.MealWorker

class WalkService : Service() {

    private var handler = Handler()
    private lateinit var runnable: Runnable

    private val fusedLocationClient = FusedLocationProviderClient(WalkableApp.instance)

    private lateinit var locationCallback: LocationCallback
    
    var contentText = ""

    private val binder = WalkerBinder()

    inner class WalkerBinder : Binder() {
        fun getService(): WalkService = this@WalkService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startTimer()

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onCreate() {
        createNotificationChannel()
        trackTimer.value = 0L
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                if (p0 != null && p0.lastLocation != null) {
                    trackerPoints.value = (trackerPoints.value ?: mutableListOf()).plus(
                        LatLng(
                            p0.lastLocation.latitude,
                            p0.lastLocation.longitude
                        )
                    ) as MutableList<LatLng>

                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
            }
        }
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        startTimer()
        startRecordingDistance()
        return binder
    }

    override fun stopService(name: Intent?): Boolean {
        stopTimer()
        trackTimer.value = 0L
        stopRecordingDistance()
        stopForeground(true)
        return super.stopService(name)

    }

    override fun unbindService(conn: ServiceConnection) {
        stopTimer()
        stopRecordingDistance()
        stopForeground(true)
        super.unbindService(conn)
    }

    override fun onDestroy() {
        trackTimer.value = 0L
        trackerPoints.value = mutableListOf()
        stopTimer()
        stopRecordingDistance()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.walkable_city)
            val descriptionText = getString(R.string.recording)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(false)
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun startTimer() {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        runnable = Runnable {
            val points = trackerPoints.value
            val minutes = (trackTimer.value ?: 0L) / 60
            val seconds = (trackTimer.value ?: 0L) % 60
            contentText = StringBuilder().append(getString(R.string.walker_timer)).append("｜")
                .append(Util.lessThenTenPadStart(minutes))
                .append(":").append(Util.lessThenTenPadStart(seconds)).append("\n")
                .append(getString(R.string.walker_distance)).append("｜")
                .append(
                    String.format(getString(R.string.recording_length), points?.toDistance() ?: 0f)
                ).toString()

            val bitmap = applicationContext.resources
                .getDrawable(R.mipmap.ic_launcher_foot_in_white_round, theme).toBitmap()

            val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.footprints)
                .setLargeIcon(bitmap)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setContentText(getString(R.string.walker_walking))
                .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentIntent(pendingIntent)

            trackTimer.value = trackTimer.value?.plus(1)
            Logger.d( "Timer ticking at : ${trackTimer.value}")
            handler.postDelayed(runnable, 1000)


            startForeground(NOTIFICATION_ID, builder.build())

        }

        handler.postDelayed(runnable, 1000)
    }

    fun stopTimer() {
        handler.removeCallbacks(runnable)
    }

    fun startRecordingDistance() {

        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopRecordingDistance() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    companion object {
        private const val CHANNEL_ID = "walking_holiday"
        private const val NOTIFICATION_ID = 0x555
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 5000L
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }
}
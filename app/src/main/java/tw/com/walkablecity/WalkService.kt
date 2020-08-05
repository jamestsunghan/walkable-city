package tw.com.walkablecity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import tw.com.walkablecity.util.Logger

class WalkService: Service() {

    private var handler = Handler()
    private lateinit var runnable: Runnable
    private var secondCount = 0
    private var trackPoints = mutableListOf<LatLng>()
    private val fusedLocationClient = FusedLocationProviderClient(WalkableApp.instance)
    private lateinit var locationCallback: LocationCallback

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onCreate() {
        createNotificationChannel()
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                if(p0 != null && p0.lastLocation != null){
                    trackPoints = trackPoints.plus(LatLng(p0.lastLocation.latitude, p0.lastLocation.longitude)) as MutableList<LatLng>

                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
            }
        }
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.walkable_city)
            val descriptionText = getString(R.string.recording)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startTimer(){

        runnable = Runnable{

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.footprints)
                .setContentTitle("Clock Ticking")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("$secondCount sec")

            with(NotificationManagerCompat.from(this)){
                notify(NOTIFICATION_ID, builder.build())
            }
            secondCount++
            Logger.d("Counting Timer ticking at : $secondCount")
            handler.postDelayed(runnable, 1000)
        }

        handler.postDelayed(runnable,1000)
    }

    private fun stopTimer(){
        handler.removeCallbacks(runnable)
    }

    private fun startRecordingDistance(){

        fusedLocationClient.requestLocationUpdates(createLocationRequest(),locationCallback, Looper.getMainLooper())
    }

    private fun stopRecordingDistance() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    companion object{
        private const val CHANNEL_ID = "walk_recording"
        private const val NOTIFICATION_ID = 0x00
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 5000L
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }
}
package tw.com.walkablecity

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.ext.toLocation
import tw.com.walkablecity.ext.toQuery
import tw.com.walkablecity.permission.RationaleDialog
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

object Util {

    private const val MAP_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?"

    fun isInternetConnected(): Boolean {
        val cm = WalkableApp.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String{
        return WalkableApp.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int{
        return WalkableApp.instance.getColor(resourceId)
    }

    fun makeShortToast(resourceId: Int){
        Toast.makeText(WalkableApp.instance, getString(resourceId),Toast.LENGTH_SHORT).show()
    }

    fun lessThenTenPadStart(time: Long): String{
        return if(time < 10){
            time.toString().padStart(2,'0')
        }else time.toString()
    }

    fun dateToTimeStamp(dateString: String): Timestamp? =
        try{
            Timestamp(requireNotNull(SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN).parse(dateString))
                .time.div(1000), 0)
        }catch (e: ParseException){
            null
        }

    fun dateAddMonth(dateString: String): String?{
        return try{

            val c = Calendar.getInstance()
            c.time = requireNotNull(SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN).parse(dateString))
            "${c.get(Calendar.YEAR)}" +
                    "-${lessThenTenPadStart((c.get(Calendar.MONTH)+2).toLong())}" +
                    "-${lessThenTenPadStart((c.get(Calendar.DAY_OF_MONTH).toLong()))}"
        }catch (e: ParseException){
            null
        }
    }




    fun calculateDistance(first: LatLng, second: LatLng): Float{
        val distance = first.toLocation().distanceTo(second.toLocation()) / 1000
        return if(distance < 0.002f){
            0F
        }else{
          distance
        }
    }

    fun requestPermission(activity: AppCompatActivity, requestId: Int,permission: String, finishActivity: Boolean ){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
            RationaleDialog.newInstance(requestId, finishActivity).show(activity.supportFragmentManager,"dialog")
        }else{
            ActivityCompat.requestPermissions(activity,arrayOf(permission),requestId)
        }
    }

    fun isPermissionGranted(grantPermissions: Array<String>, grantResults: IntArray, permission: String
    ): Boolean{
        for(i in grantPermissions.indices){
            if(permission == grantPermissions[i]){
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }

    fun setDp(num: Float): Float{
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP
            , num, WalkableApp.instance.resources.displayMetrics)
    }

//    fun constructUrl(center: GeoPoint, zoom: Int, path: List<GeoPoint>): String{
//        return StringBuilder().append(MAP_BASE_URL).append("center=${center.toQuery()}").toString()
//    }

    fun putDataToSharedPreference(key: String, accumulated: Float? = null, count: Int? = null){
        if(accumulated != null){
            WalkableApp.instance.getSharedPreferences(key, Context.MODE_PRIVATE).edit()
                .putFloat(key, accumulated)
                .apply()
        }
        if(count != null){
            WalkableApp.instance.getSharedPreferences(key, Context.MODE_PRIVATE).edit()
                .putInt(key, count)
                .apply()
        }
    }

    fun getAccumulatedFromSharedPreference(key: String, userData: Float): Float{
        val data = WalkableApp.instance.getSharedPreferences(key, Context.MODE_PRIVATE).getFloat(key, -1f)

        return when{
            data < 0f -> {
                putDataToSharedPreference(key, userData)
                WalkableApp.instance.getSharedPreferences(key, Context.MODE_PRIVATE).getFloat(key, -1f)
            }
            else ->data
        }
    }

    fun getCountFromSharedPreference(key:String): Int{
        return WalkableApp.instance.getSharedPreferences(key, Context.MODE_PRIVATE).getInt(key, -1)
    }




    const val ACCU_KM      = "accumulated_km"
    const val ACCU_HOUR    = "accumulated_hour"
    const val EVENT_COUNT  = "event_count"
    const val FRIEND_COUNT = "friend_count"

}
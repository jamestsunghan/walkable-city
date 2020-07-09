package tw.com.walkablecity

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.ext.toLocation
import tw.com.walkablecity.ext.toQuery
import tw.com.walkablecity.permission.RationaleDialog
import java.lang.StringBuilder
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

    fun constructUrl(center: GeoPoint, zoom: Int, path: List<GeoPoint>): String{
        return StringBuilder().append(MAP_BASE_URL).append("center=${center.toQuery()}").toString()
    }
}
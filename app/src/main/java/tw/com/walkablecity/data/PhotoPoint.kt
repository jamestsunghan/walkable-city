package tw.com.walkablecity.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PhotoPoint(

    val point: @RawValue GeoPoint? = null,
    val photo: String = ""
): Parcelable{

    fun getLatLngPoint(): LatLng?{
         return if (point == null) {
             null
         }else{
             LatLng(point.latitude, point.longitude)
         }
    }

    fun drawBitmap(size: Int): Bitmap {
        return BitmapFactory.decodeFile(
            photo,
            BitmapFactory.Options().apply {
                inSampleSize = size
            })
    }


}
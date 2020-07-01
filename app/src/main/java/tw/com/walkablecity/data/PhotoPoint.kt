package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PhotoPoint(

    val point: @RawValue GeoPoint,
    val photo: String
): Parcelable
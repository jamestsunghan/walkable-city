package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bound (
    val southwest: LatLngResult? = null,
    val northeast: LatLngResult? = null
): Parcelable
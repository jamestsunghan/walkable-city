package tw.com.walkablecity.data.directionresult

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LatLngResult (
    val lat: Double? = null,
    val lng: Double? = null
): Parcelable
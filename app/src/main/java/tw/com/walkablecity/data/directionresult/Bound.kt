package tw.com.walkablecity.data.directionresult

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bound (
    val southwest: LatLngResult? = null,
    val northeast: LatLngResult? = null
): Parcelable
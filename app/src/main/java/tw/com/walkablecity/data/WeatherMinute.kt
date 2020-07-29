package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class WeatherMinute (
    val dt: Long? = null,
    val precipitation: Float? = null
): Parcelable

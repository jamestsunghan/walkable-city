package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherItem (
    val id: Int? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
): Parcelable

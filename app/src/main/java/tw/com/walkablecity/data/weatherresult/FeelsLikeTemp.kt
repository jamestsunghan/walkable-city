package tw.com.walkablecity.data.weatherresult

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeelsLikeTemp (
    val day: Float? = null,
    val night: Float? = null,
    val eve: Float? = null,
    val morn: Float? = null
): Parcelable

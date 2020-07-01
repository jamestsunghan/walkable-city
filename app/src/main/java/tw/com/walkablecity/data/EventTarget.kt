package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventTarget (
    val frequencyType: FrequencyType? = null,
    val frequency: Int? = null,
    val distance: Float? = null,
    val hour: Float? = null
): Parcelable
package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventTarget (
    var frequencyType: FrequencyType? = null,
    var distance: Float? = null,
    var hour: Float? = null
): Parcelable
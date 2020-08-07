package tw.com.walkablecity.data.directionresult

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WalkDuration (
    val value: Int? = null,
    val text: String? = null
): Parcelable

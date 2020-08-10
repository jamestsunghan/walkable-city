package tw.com.walkablecity.data.directionresult

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WalkDistance (
    val value: Long? = null,
    val text: String? = null
): Parcelable

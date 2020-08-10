package tw.com.walkablecity.data.directionresult

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WalkPath (
    val points: String? = null
): Parcelable

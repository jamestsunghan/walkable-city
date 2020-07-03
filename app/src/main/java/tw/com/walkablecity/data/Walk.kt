package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Walk (
    val distance: Float,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val duration: Long,
    val routeId: String
): Parcelable
package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event (
    val id: Long,
    val title: String,
    val type: EventType,
    val target: EventTarget,
    val host: String,
    val invited: List<String>,
    val member: List<String>,
    val startDate: Timestamp,
    val endDate: Timestamp
): Parcelable
package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event (
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val isPublic: Boolean? = null,
    val type: EventType? = null,
    val target: EventTarget? = null,
    val host: Int? = null,
    val invited: List<Int> = listOf(),
    val member: List<Int> = listOf(),
    val memberCount: Int? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val status: EventStatus? = null
): Parcelable
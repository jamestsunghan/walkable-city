package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event (
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val public: Boolean? = null,
    val type: EventType? = null,
    val target: EventTarget? = null,
    val host: String? = null,
    val invited: List<String> = listOf(),
    val member: List<Friend> = listOf(),
    val memberCount: Int? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val status: EventStatus? = null
): Parcelable
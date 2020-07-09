package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment (
    val userId: Int? = null,
    val recommend: Int? = null,
    val content: String? = null,
    val createTime: Timestamp? = null
): Parcelable
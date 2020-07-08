package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment (
    val userId: Int,
    val recommend: Int,
    val content: String,
    val createTime: Timestamp
): Parcelable
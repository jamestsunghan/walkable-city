package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment (
    val userId: String? = null,
    val recommend: Int? = null,
    val content: String? = null,
    val createTime: Timestamp? = null
): Parcelable{

    fun toHashMap(): HashMap<String, Any?>{
        return hashMapOf(
            "content" to this.content,
            "createTime" to this.createTime,
            "recommend" to this.recommend,
            "userId" to this.userId
        )
    }


}
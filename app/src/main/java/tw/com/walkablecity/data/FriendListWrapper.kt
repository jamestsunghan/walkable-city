package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FriendListWrapper(
    val data: List<Friend> = listOf()
): Parcelable
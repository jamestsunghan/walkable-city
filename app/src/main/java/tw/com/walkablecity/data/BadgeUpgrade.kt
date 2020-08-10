package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BadgeUpgrade (
    var home: Int = 0,
    var event: Int = 0,
    var friend: Int = 0
): Parcelable{
    fun sum(): Int = home + event + friend
}
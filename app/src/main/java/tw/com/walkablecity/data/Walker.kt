package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import tw.com.walkablecity.home.WalkerStatus

@Parcelize
data class Walker (
    val status: WalkerStatus
): Parcelable
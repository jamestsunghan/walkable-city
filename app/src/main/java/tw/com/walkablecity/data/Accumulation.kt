package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Accumulation (
    val daily: Float,
    val weekly: Float,
    val monthly: Float,
    val yearly: Float,
    val total: Float
): Parcelable
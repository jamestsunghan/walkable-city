package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Accumulation (
    val daily: Float   = 0f,
    val weekly: Float  = 0f,
    val monthly: Float = 0f,
    val yearly: Float  = 0f,
    val total: Float   = 0f
): Parcelable
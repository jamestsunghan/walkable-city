package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Accumulation (
    val day: Float,
    val week: Float,
    val month: Float,
    val year: Float,
    val total: Float
): Parcelable
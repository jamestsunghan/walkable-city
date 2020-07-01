package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteRating (
    val coverage: Float,
    val rest: Float,
    val scenery: Float,
    val snack: Float,
    val tranquility: Float,
    val vibe: Float
): Parcelable
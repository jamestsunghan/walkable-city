package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteRating (
    val coverage: Float = 0f,
    val rest: Float = 0f,
    val scenery: Float= 0f,
    val snack: Float= 0f,
    val tranquility: Float= 0f,
    val vibe: Float= 0f
): Parcelable{
    fun sum(): Float = coverage + rest + scenery + snack + tranquility + vibe
    fun average(): Float = sum().div(6)
}
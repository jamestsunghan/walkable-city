package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Route(
    val id: Long = 0L,
    val title: String? = null,
    val description: String? = null,
    val ratingAvr: RouteRating? = null,
    @Json(name = "map_image")val mapImage: String? = null,
    val length: Float = 0f,
    val minutes: Float = 0f,
    val walkers: List<Int> = listOf(),
    val followers: List<Int> = listOf(),
    val comments: List<Comment> = listOf(),
    val photopoints: List<PhotoPoint> = listOf(),
    val ratings: List<RouteRating> = listOf(),
    val waypoints: @RawValue List<GeoPoint> = listOf()

    ): Parcelable
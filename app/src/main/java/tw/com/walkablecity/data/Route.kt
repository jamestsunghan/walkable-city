package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Route(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val ratingAvr: RouteRating? = null,
    @Json(name = "map_image")val mapImage: String? = null,
    val length: Float = 0f,
    val minutes: Float = 0f,
    val walkers: List<String> = listOf(),
    val followers: List<String> = listOf(),
    val comments: List<Comment> = listOf(),
    val photopoints: List<PhotoPoint> = listOf(),
    val ratings: List<RouteRating> = listOf(),
    var waypoints:@RawValue List< @RawValue GeoPoint> = listOf(),
    var waypointsLatLng: List<LatLng> = listOf()

    ): Parcelable
package tw.com.walkablecity.data.directionresult

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DirectionResult (
    val error: String? = null,
    val status: String? = null,
    @Json(name = "geocoded_waypoints")val geoWaypoints: List<GeoWaypoint> = listOf(),
    val routes: List<GoogleRoute> = listOf(),
    val waypoints: List<LatLng> = listOf()
): Parcelable
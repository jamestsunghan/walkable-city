package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GoogleRoute (
    val summary: String? = null,
    val legs: List<Leg> = listOf(),
    val copyrights: String? = null,
    @Json(name = "overview_polyline")val overviewPolyline: WalkPath? = null,
    val warnings: List<String> = listOf(),
    @Json(name = "waypoint_order")val waypointOrder: List<Int> = listOf(),
    val bounds: Bound? = null
): Parcelable
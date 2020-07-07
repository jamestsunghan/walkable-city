package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Leg (
    val steps: List<Step> = listOf(),
    val duration: WalkDuration? = null,
    val distance: WalkDistance? = null,
    @Json(name = "start_location")val startLocation: LatLngResult? = null,
    @Json(name = "end_location")val endLocation: LatLngResult? = null,
    @Json(name = "start_address")val startAddress: String? = null,
    @Json(name = "end_address")val endAddress: String? = null,
    @Json(name = "traffic_speend_entry")val trafficSpeedEntry: String? = null,
    @Json(name = "via_waypoint")val viaWaypoint: List<LatLngResult> = listOf()
    ): Parcelable

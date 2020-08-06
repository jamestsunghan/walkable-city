package tw.com.walkablecity.data.directionresult

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Step (
    @Json(name = "travel_mode")val travelMode: String? = null,
    @Json(name = "start_location")val startLocation: LatLngResult? = null,
    @Json(name = "end_location")val endLocation: LatLngResult? = null,
    val polyline: @RawValue WalkPath? = null,
    val maneuver: String? = null,
    val duration: WalkDuration? = null,
    @Json(name = "html_instructions")val HtmlInstructions: String? = null,
    val distance: WalkDistance? = null
    ): Parcelable{
}

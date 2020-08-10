package tw.com.walkablecity.data.directionresult

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeoWaypoint (
    @Json(name = "geocoder_status")val geoStatus: String? = null,
    @Json(name = "place_id")val placeId: String? = null,
    val types: List<String> = listOf()
    ): Parcelable

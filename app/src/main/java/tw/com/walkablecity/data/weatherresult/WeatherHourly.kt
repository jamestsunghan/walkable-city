package tw.com.walkablecity.data.weatherresult

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherHourly(
    val dt: Long? = null,
    val temp: Float? = null,
    @Json(name = "feels_like")val feelsLike: Float? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    @Json(name = "dew_point")val dewPoint: Float? = null,
    val clouds: Int? = null,
    val visibility: Int? = null,
    @Json(name = "wind_speed")val windSpeed: Float? = null,
    @Json(name = "wind_deg")val windDeg: Float? = null,
    val weather: List<WeatherItem> = listOf(),
    val pop: Float? = null,
    val rain: Rain? = null
): Parcelable
package tw.com.walkablecity.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherResult(
    val error: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val timezone: String? = null,
    @Json(name = "timezone_offset")val timezoneOffset: Int? = null,
    val current: Current? = null,
    val minutely: List<WeatherMinute> = listOf(),
    val hourly: List<WeatherHourly> = listOf(),
    val daily: List<WeatherDaily> = listOf()
): Parcelable
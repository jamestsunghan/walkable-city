package tw.com.walkablecity.data.weatherresult

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
class Rain (
    @Json(name = "1h")val oneHour: Float? = null
): Parcelable

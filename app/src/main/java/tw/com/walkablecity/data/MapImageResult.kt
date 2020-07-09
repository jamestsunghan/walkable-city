package tw.com.walkablecity.data

import android.media.Image
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class MapImageResult (
    val error: String? = null,
    val image: @RawValue Image
): Parcelable
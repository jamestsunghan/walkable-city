package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Route(
    val id: Long,
    val title: String,
    val description: String,
    val ratingAvr: RouteRating,
    val mapImage: String,
    val comments: List<Comment>,
    val photoPoints: List<PhotoPoint>,
    val ratings: List<RouteRating>,
    val wayPoints: @RawValue List<GeoPoint>

    ): Parcelable
package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Walk (
    val distance: Float,
    val startTime: Timestamp,
    val endTime: Timestamp,
    val duration: Long,
    val routeId: String?,
    val waypoints: List<LatLng> = listOf()
): Parcelable
package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Walk (
    val distance: Float? = null,
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val duration: Long? = null,
    val routeId: String? = null,
    val waypoints: List<LatLng> = listOf()
): Parcelable
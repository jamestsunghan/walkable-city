package tw.com.walkablecity.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MissionFQ (
    val date: Timestamp? = null,
    val accomplish: Float? = null
): Parcelable
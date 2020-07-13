package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val id: String,
    val idCustom: String? = null,
    val name: String?,
    val picture: String?,
    val email: String?,
    val accumulatedKm: Accumulation,
    val accumulatedHour: Accumulation,
    val friends: List<Friend>,
    val walks: List<Walk>
): Parcelable
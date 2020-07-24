package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val id: String? = null,
    val idCustom: String? = null,
    val name: String? = null,
    val picture: String? = null,
    val email: String? = null,
    val accumulatedKm: Accumulation? = null,
    val accumulatedHour: Accumulation? = null,
    val friends: List<Friend> = listOf(),
    val walks: List<Walk> = listOf(),
    var weather: Boolean = false,
    var meal: Boolean = false
): Parcelable
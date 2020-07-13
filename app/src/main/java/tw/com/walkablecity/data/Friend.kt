package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Friend (
    val id: String? = null,
    val idCustom: String? = null,
    val name: String? = null,
    val picture: String? = null,
    val email: String? = null
): Parcelable
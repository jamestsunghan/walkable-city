package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Friend (
    val id: String,
    val name: String,
    val picture: String,
    val email: String
): Parcelable
package tw.com.walkablecity.event

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString

@Parcelize
enum class EventPageType(val title: String, val empty: String):Parcelable {
    CHALLENGING(getString(R.string.event_challenging_title), getString(R.string.event_empty_challenging)),
    POPULAR(getString(R.string.event_popular_title), getString(R.string.event_empty_popular)),
    INVITED(getString(R.string.event_inviting_title), getString(R.string.event_empty_inviting))
}
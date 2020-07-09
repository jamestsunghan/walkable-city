package tw.com.walkablecity.event

import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString

enum class EventPageType(val title: String, val empty: String) {
    CHALLENGING(getString(R.string.event_challenging_title), getString(R.string.event_empty_challenging)),
    POPULAR(getString(R.string.event_popular_title),getString(R.string.event_empty_popular)),
    INVITED(getString(R.string.event_inviting_title),getString(R.string.event_empty_inviting))
}
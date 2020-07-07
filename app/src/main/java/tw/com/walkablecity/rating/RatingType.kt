package tw.com.walkablecity.rating

import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString

enum class RatingType(val title: String, val emptyRoute: String) {
    ROUTE(getString(R.string.rating_route_title), getString(R.string.no_load_route)),
    WALK(getString(R.string.rating_walk_route_title), getString(R.string.no_rating_for_walk))
}
package tw.com.walkablecity.loadroute


import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString

enum class LoadRouteType(val title: String) {
    MINE(getString(R.string.route_mine_title)),
    FAVORITE(getString(R.string.route_favorite_title)),
    NEARBY(getString(R.string.route_nearby_title))
}
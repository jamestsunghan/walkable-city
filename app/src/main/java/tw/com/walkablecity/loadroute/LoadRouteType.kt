package tw.com.walkablecity.loadroute


import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString

enum class LoadRouteType(val title: String, val emptyRoute: String) {
    MINE(getString(R.string.route_mine_title), getString(R.string.no_route_yet)),
    FAVORITE(getString(R.string.route_favorite_title), getString(R.string.no_favs_route_yet)),
    NEARBY(getString(R.string.route_nearby_title), getString(R.string.no_route_nearby))
}
package tw.com.walkablecity


import tw.com.walkablecity.util.Util.getString

enum class CurrentFragmentType(val title: String) {

    HOME(getString(R.string.home_title)),
    RANKING(getString(R.string.ranking_title)),
    EVENT(getString(R.string.event_title)),
    FAVORITE(getString(R.string.favorite_title)),
    PROFILE(getString(R.string.profile_title)),
    SEARCH(""),

    DETAIL(getString(R.string.detail_title)),

    LOAD_ROUTE(getString(R.string.load_route)),
    RATING(getString(R.string.rating_title)),

    BADGE(getString(R.string.badge_title)),
    SETTINGS(getString(R.string.setting_title)),
    BEST_WALKERS(getString(R.string.best_walker_title)),
    EXPLORE(getString(R.string.explore_title)),

    EVENT_DETAIL(getString(R.string.event_more_info)),
    HOST(getString(R.string.create_event)),

    LOGIN(""),
    ADD_FRIEND(getString(R.string.add_friend_title)),
    ADD_2_EVENT(getString(R.string.add_friend_event_title)),

    CREATE_ROUTE_DIALOG("")
}
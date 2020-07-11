package tw.com.walkablecity.data

import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString

enum class EventType(val title: String, val prefix: String) {
    FREQUENCY(getString(R.string.filter_frequency), "FR"),
    DISTANCE_GROUP(getString(R.string.filter_distance_group), "DG"),
    DISTANCE_RACE(getString(R.string.filter_distance_race), "DR"),
    HOUR_GROUP(getString(R.string.filter_hour_group), "HG"),
    HOUR_RACE(getString(R.string.filter_hour_race), "HR")
}
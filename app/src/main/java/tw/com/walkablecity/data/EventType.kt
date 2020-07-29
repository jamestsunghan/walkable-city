package tw.com.walkablecity.data

import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getColor
import tw.com.walkablecity.Util.getString

enum class EventType(val title: String, val prefix: String, val colorList: List<Int>) {
    FREQUENCY(getString(R.string.filter_frequency), "FR"
        , listOf(getColor(R.color.event_frequency), getColor(R.color.event_frequency_darker), getColor(R.color.event_frequency_darkest))),
    DISTANCE_GROUP(getString(R.string.filter_distance_group), "DG"
        , listOf(getColor(R.color.event_distance_group), getColor(R.color.event_distance_group_darker), getColor(R.color.event_distance_group_darkest))),
    DISTANCE_RACE(getString(R.string.filter_distance_race), "DR"
        , listOf(getColor(R.color.event_distance_race), getColor(R.color.event_distance_race_darker), getColor(R.color.event_distance_race_darkest))),
    HOUR_GROUP(getString(R.string.filter_hour_group), "HG"
        , listOf(getColor(R.color.event_hour_group), getColor(R.color.event_hour_group_darker), getColor(R.color.event_hour_group_darkest))),
    HOUR_RACE(getString(R.string.filter_hour_race), "HR"
        , listOf(getColor(R.color.event_hour_race), getColor(R.color.event_hour_race_darker), getColor(R.color.event_hour_race_darkest)))
}
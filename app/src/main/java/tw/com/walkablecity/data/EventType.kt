package tw.com.walkablecity.data

import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getColor
import tw.com.walkablecity.util.Util.getString

enum class EventType(
    val title: String,
    val prefix: String,
    val eventCreateTitle: String,
    val colorList: List<Int>
) {
    FREQUENCY(
        getString(R.string.filter_frequency),
        "FR",
        getString(R.string.create_event_frequency_title)
        , listOf(
            getColor(R.color.event_frequency),
            getColor(R.color.event_frequency_darker),
            getColor(R.color.event_frequency_darkest)
        )
    ),

    DISTANCE_GROUP(
        getString(R.string.filter_distance_group),
        "DG",
        getString(R.string.create_event_distance_title)
        , listOf(
            getColor(R.color.event_distance_group),
            getColor(R.color.event_distance_group_darker),
            getColor(R.color.event_distance_group_darkest)
        )
    ),

    DISTANCE_RACE(
        getString(R.string.filter_distance_race),
        "DR",
        getString(R.string.create_event_distance_title)
        , listOf(
            getColor(R.color.event_distance_race),
            getColor(R.color.event_distance_race_darker),
            getColor(R.color.event_distance_race_darkest)
        )
    ),

    HOUR_GROUP(
        getString(R.string.filter_hour_group),
        "HG",
        getString(R.string.create_event_hour_title)
        , listOf(
            getColor(R.color.event_hour_group),
            getColor(R.color.event_hour_group_darker),
            getColor(R.color.event_hour_group_darkest)
        )
    ),

    HOUR_RACE(
        getString(R.string.filter_hour_race),
        "HR",
        getString(R.string.create_event_hour_title)
        , listOf(
            getColor(R.color.event_hour_race),
            getColor(R.color.event_hour_race_darker),
            getColor(R.color.event_hour_race_darkest)
        )
    );

    fun buildTargetString(goal: EventTarget): String {
        val end = if (goal.distance == null) {
            String.format(getString(R.string.walk_accumulate_hours), goal.hour)
        } else {
            String.format(getString(R.string.walk_accumulate_km), goal.distance)
        }
        return StringBuilder().apply {
            when (this@EventType) {
                FREQUENCY      -> append(getString(R.string.event_goal)).append(goal.frequencyType?.text)
                DISTANCE_GROUP -> append(getString(R.string.event_group_goal))
                DISTANCE_RACE  -> append(getString(R.string.event_race_goal))
                HOUR_GROUP     -> append(getString(R.string.event_group_goal))
                HOUR_RACE      -> append(getString(R.string.event_race_goal))

            }
        }.append(end).toString()
    }


}
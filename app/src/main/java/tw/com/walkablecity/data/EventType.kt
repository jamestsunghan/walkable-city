package tw.com.walkablecity.data

import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getColor
import tw.com.walkablecity.util.Util.getString

enum class EventType(
    val title: String,
    val prefix: String,
    val colorId: Int,
    val colorList: List<Int>
) {
    FREQUENCY(
        getString(R.string.filter_frequency),
        "FR",
        R.color.event_frequency
        , listOf(
            getColor(R.color.event_frequency),
            getColor(R.color.event_frequency_darker),
            getColor(R.color.event_frequency_darkest)
        )
    ),

    DISTANCE_GROUP(
        getString(R.string.filter_distance_group),
        "DG",
        R.color.event_distance_group
        , listOf(
            getColor(R.color.event_distance_group),
            getColor(R.color.event_distance_group_darker),
            getColor(R.color.event_distance_group_darkest)
        )
    ),

    DISTANCE_RACE(
        getString(R.string.filter_distance_race),
        "DR",
        R.color.event_distance_race
        , listOf(
            getColor(R.color.event_distance_race),
            getColor(R.color.event_distance_race_darker),
            getColor(R.color.event_distance_race_darkest)
        )
    ),

    HOUR_GROUP(
        getString(R.string.filter_hour_group),
        "HG",
        R.color.event_hour_group
        , listOf(
            getColor(R.color.event_hour_group),
            getColor(R.color.event_hour_group_darker),
            getColor(R.color.event_hour_group_darkest)
        )
    ),

    HOUR_RACE(
        getString(R.string.filter_hour_race),
        "HR",
        R.color.event_hour_race
        , listOf(
            getColor(R.color.event_hour_race),
            getColor(R.color.event_hour_race_darker),
            getColor(R.color.event_hour_race_darkest)
        )
    );

    fun buildTargetString(goal: EventTarget): String {
        val end =
            if (goal.distance == null) getString(R.string.walk_accumulate_hours) else getString(R.string.walk_accumulate_km)
        return when (this) {
            FREQUENCY -> StringBuilder().append(getString(R.string.event_goal)).append(goal.frequencyType?.text).append(
                String.format(end, goal.distance ?: goal.hour)
            ).toString()
            DISTANCE_GROUP -> StringBuilder().append(getString(R.string.event_group_goal)).append(
                String.format(getString(R.string.walk_accumulate_km), goal.distance)
            ).toString()
            DISTANCE_RACE -> StringBuilder().append(getString(R.string.event_race_goal)).append(
                String.format(getString(R.string.walk_accumulate_km), goal.distance)
            ).toString()
            HOUR_GROUP -> StringBuilder().append(getString(R.string.event_group_goal)).append(
                String.format(
                    getString(R.string.walk_accumulate_hours),
                    goal.hour
                )
            ).toString()
            HOUR_RACE -> StringBuilder().append(getString(R.string.event_race_goal)).append(
                String.format(
                    getString(R.string.walk_accumulate_hours),
                    goal.hour
                )
            ).toString()

        }
    }

    fun hourType() = this == HOUR_RACE || this == HOUR_GROUP
    fun distanceType() = this == DISTANCE_RACE || this == DISTANCE_GROUP
    fun raceType() = this == HOUR_RACE || this == DISTANCE_RACE
    fun groupType() = this == HOUR_GROUP || this == DISTANCE_GROUP


    val eventCreateTitle = when {
        hourType() -> getString(R.string.create_event_hour_title)
        distanceType() -> getString(R.string.create_event_distance_title)
        else -> getString(R.string.create_event_frequency_title)
    }
}
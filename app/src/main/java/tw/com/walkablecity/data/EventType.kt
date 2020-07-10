package tw.com.walkablecity.data

enum class EventType(val title: String, val prefix: String) {
    FREQUENCY("frequency", "FR"),
    DISTANCE_GROUP("distance_group", "DG"),
    DISTANCE_RACE("distance_race", "DR"),
    HOUR_GROUP("hour_group", "HG"),
    HOUR_RACE("hour_race", "HR")
}
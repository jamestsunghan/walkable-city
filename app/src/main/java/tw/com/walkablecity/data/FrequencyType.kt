package tw.com.walkablecity.data


import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString

enum class FrequencyType(val text: String, val ranking: String) {
    DAILY(getString(R.string.frequency_day), getString(R.string.daily_ranking)),
    WEEKLY(getString(R.string.frequency_week), getString(R.string.week_ranking)),
    MONTHLY(getString(R.string.frequency_month), getString(R.string.month_ranking))
}
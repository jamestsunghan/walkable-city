package tw.com.walkablecity.data


import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString
import java.util.*

enum class FrequencyType(val text: String, val ranking: String, val timeUnit: Int) {
    DAILY(getString(R.string.frequency_day), getString(R.string.daily_ranking), FrequencyType.ONE_DAY),
    WEEKLY(getString(R.string.frequency_week), getString(R.string.week_ranking), FrequencyType.ONE_WEEK),
    MONTHLY(getString(R.string.frequency_month), getString(R.string.month_ranking), Calendar.MONTH);
    companion object{
        private const val ONE_DAY = 24 * 60 * 60
        private const val ONE_WEEK = 7 * ONE_DAY
    }
}
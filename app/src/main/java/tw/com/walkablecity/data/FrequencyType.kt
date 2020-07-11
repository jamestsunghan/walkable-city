package tw.com.walkablecity.data


import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString

enum class FrequencyType(val text: String) {
    DAILY(getString(R.string.frequency_day)),
    WEEKLY(getString(R.string.frequency_week)),
    MONTHLY(getString(R.string.frequency_month))
}
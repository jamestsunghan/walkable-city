package tw.com.walkablecity.data

import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString

enum class AccumulationType(val text: String) {
    DAILY(getString(R.string.accumulate_daily)),
    WEEKLY(getString(R.string.accumulate_weekly)),
    MONTHLY(getString(R.string.accumulate_monthly)),
    YEARLY(getString(R.string.accumulate_yearly)),
    TOTAL(getString(R.string.accumulate_total))
}
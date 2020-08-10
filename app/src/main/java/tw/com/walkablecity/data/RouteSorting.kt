package tw.com.walkablecity.data

import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString

enum class RouteSorting(val text: String, val title: String) {
    COVERAGE("coverage", getString(R.string.filter_coverage)),
    REST("rest", getString(R.string.filter_rest)),
    SCENERY("scenery", getString(R.string.filter_scenery)),
    SNACK("snack", getString(R.string.filter_snack)),
    TRANQUILITY("tranquility", getString(R.string.filter_tranquility)),
    VIBE("vibe", getString(R.string.filter_vibe))
}
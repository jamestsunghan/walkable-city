package tw.com.walkablecity

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.loadroute.route.RouteItem
import tw.com.walkablecity.loadroute.route.RouteItemAdapter


@BindingAdapter("pause")
fun walkPausing(view: TextView, status: WalkerStatus) {
    when (status) {
        WalkerStatus.PREPARE -> {
            view.visibility = View.GONE
        }
        WalkerStatus.WALKING -> {
            view.visibility = View.VISIBLE
            view.text = Util.getString(R.string.pause_walking)
        }
        WalkerStatus.PAUSING -> {
            view.visibility = View.VISIBLE
            view.text = Util.getString(R.string.resume_walking)
        }
        WalkerStatus.FINISH -> {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("routeItem")
fun addFilterAndSubmitList(view: RecyclerView, list: List<Route>?){
    val items = when(list){
        null -> listOf(RouteItem.Filter)
        else -> listOf(RouteItem.Filter) + list.map{RouteItem.LoadRoute(it)}
    }

    view.adapter.apply {
        when(this){
            is RouteItemAdapter -> submitList(items)
        }
    }

}

//@BindingAdapter("filter")
//fun setFilterButton(textView: TextView, sorting: RouteSorting){
//
//}

//@InverseBindingAdapter(attribute = "filter")
//fun setLiveDataFilter(textView: TextView, sorting: RouteSorting): RouteSorting{
//
//    return when(textView.id){
//        R.id.filter_coverage -> RouteSorting.COVERAGE
//        R.id.filter_rest -> RouteSorting.REST
//        R.id.filter_scenery -> RouteSorting.SCENERY
//        R.id.filter_snack -> RouteSorting.SNACK
//        R.id.filter_tranquility -> RouteSorting.TRANQUILITY
//        R.id.filter_vibe -> RouteSorting.VIBE
//        else ->sorting
//    }
//}




//mock data area

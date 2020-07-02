package tw.com.walkablecity.loadroute.route

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.databinding.ItemRouteFilterBinding
import tw.com.walkablecity.databinding.ItemRouteLinearBinding

class RouteItemAdapter(private val viewModel: RouteItemViewModel): ListAdapter<RouteItem, RecyclerView.ViewHolder>(DiffCallback) {



    class FilterViewHolder(private val binding: ItemRouteFilterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(viewModel: RouteItemViewModel){
            binding.viewModel =viewModel

            binding.filterCoverage.setOnClickListener {
                viewModel.filter.value = RouteSorting.COVERAGE

            }
            binding.filterRest.setOnClickListener {
                viewModel.filter.value = RouteSorting.REST
            }
            binding.filterScenery.setOnClickListener {
                viewModel.filter.value = RouteSorting.SCENERY
            }
            binding.filterSnack.setOnClickListener {
                viewModel.filter.value = RouteSorting.SNACK
            }
            binding.filterTranquility.setOnClickListener {
                viewModel.filter.value = RouteSorting.TRANQUILITY
            }
            binding.filterVibe.setOnClickListener {
                viewModel.filter.value = RouteSorting.VIBE
            }
            binding.executePendingBindings()
        }
    }

    class RouteViewHolder(private val binding: ItemRouteLinearBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(route: Route, viewModel: RouteItemViewModel){
            val filterList = listOf(
                Pair(Util.getString(R.string.filter_coverage),route.ratingAvr?.coverage),
                Pair(Util.getString(R.string.filter_tranquility),route.ratingAvr?.tranquility),
                Pair(Util.getString(R.string.filter_scenery),route.ratingAvr?.scenery),
                Pair(Util.getString(R.string.filter_rest),route.ratingAvr?.rest),
                Pair(Util.getString(R.string.filter_snack),route.ratingAvr?.snack),
                Pair(Util.getString(R.string.filter_vibe),route.ratingAvr?.vibe))
            val sortList = filterList.sortedBy { it.second }.reversed().map{
                "${it.first} | ${it.second}"
            }
            Log.d("JJ","sortList ${sortList} size ${sortList.size}")
            binding.characterSpinner.adapter = CharacterSpinnerAdapter(sortList)
            binding.route = route
            binding.selectRoute.setOnClickListener {
                viewModel.selectRoute.value = route
            }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<RouteItem>(){
        override fun areItemsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TYPE_FILTER   = 0x00
        private const val ITEM_VIEW_TYPE_ROUTE    = 0x01

    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is RouteItem.Filter -> ITEM_VIEW_TYPE_FILTER
            is RouteItem.LoadRoute -> ITEM_VIEW_TYPE_ROUTE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE_FILTER -> FilterViewHolder(ItemRouteFilterBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            ITEM_VIEW_TYPE_ROUTE -> RouteViewHolder(ItemRouteLinearBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is RouteViewHolder -> holder.bind((getItem(position) as RouteItem.LoadRoute).route, viewModel)
            is FilterViewHolder -> holder.bind(viewModel)
        }
    }
}

sealed class RouteItem{
    abstract val id: Long

    object Filter: RouteItem(){
        override val id: Long
            get() = Long.MIN_VALUE
    }

    data class LoadRoute(val route: Route): RouteItem(){
        override val id: Long
            get() = route.id
    }
}
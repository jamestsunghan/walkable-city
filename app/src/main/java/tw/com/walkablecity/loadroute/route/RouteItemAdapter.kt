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
import tw.com.walkablecity.data.RouteRating
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.databinding.ItemRouteFilterBinding
import tw.com.walkablecity.databinding.ItemRouteLinearBinding
import tw.com.walkablecity.ext.toSortList

class RouteItemAdapter(private val viewModel: RouteItemViewModel): ListAdapter<RouteItem, RecyclerView.ViewHolder>(DiffCallback) {



    class FilterViewHolder(private val binding: ItemRouteFilterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(viewModel: RouteItemViewModel){

            binding.viewModel = viewModel
            binding.timeSlider.addOnChangeListener { slider, value, fromUser ->
                viewModel.setTimeFilter(slider.values)
            }
            binding.executePendingBindings()
        }
    }

    class RouteViewHolder(private val binding: ItemRouteLinearBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(route: Route, viewModel: RouteItemViewModel){

            Log.d("JJ","sortList ${route.ratingAvr.toSortList()} ratingAvr ${route.ratingAvr}")
            binding.characterSpinner.adapter = CharacterSpinnerAdapter(route.ratingAvr.toSortList())
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


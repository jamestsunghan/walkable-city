package tw.com.walkablecity.loadroute.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.databinding.ItemRouteFilterBinding
import tw.com.walkablecity.databinding.ItemRouteLinearBinding

class RouteItemAdapter: ListAdapter<RouteItem, RecyclerView.ViewHolder>(DiffCallback) {



    class FilterViewHolder(private val binding: ItemRouteFilterBinding): RecyclerView.ViewHolder(binding.root){

    }

    class RouteViewHolder(private val binding: ItemRouteLinearBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(route: Route){
            binding.route = route
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<RouteItem>(){
        override fun areItemsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
            return oldItem == newItem
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
            is RouteViewHolder -> holder.bind((getItem(position) as RouteItem.LoadRoute).route)
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
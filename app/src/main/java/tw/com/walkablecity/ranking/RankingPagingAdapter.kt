package tw.com.walkablecity.ranking

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.databinding.ItemRankingFilterBinding
import tw.com.walkablecity.databinding.ItemRankingLinearBinding
import tw.com.walkablecity.databinding.ItemRouteFilterBinding
import tw.com.walkablecity.databinding.ItemRouteLinearBinding
import tw.com.walkablecity.ext.toSortList
import tw.com.walkablecity.loadroute.route.CharacterSpinnerAdapter
import tw.com.walkablecity.loadroute.route.RouteItem
import tw.com.walkablecity.loadroute.route.RouteItemAdapter
import tw.com.walkablecity.loadroute.route.RouteItemViewModel

class RankingPagingAdapter(private val onClickListener: OnClickListener, private val viewModel: RankingViewModel): PagedListAdapter<RouteItem, RecyclerView.ViewHolder>(RouteItemAdapter.DiffCallback){

    class FilterViewHolder(private val binding: ItemRankingFilterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(viewModel: RankingViewModel){

            binding.viewModel = viewModel
            binding.timeSlider.addOnChangeListener { slider, value, fromUser ->
                viewModel.setTimeFilter(slider.values)
            }
            binding.executePendingBindings()
        }
    }

    class RouteViewHolder(private val binding: ItemRankingLinearBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(route: Route, viewModel: RankingViewModel){

            Log.d("JJ","sortList ${route.ratingAvr.toSortList(viewModel.filter.value)} ratingAvr ${route.ratingAvr}")
            binding.characterSpinner.adapter = CharacterSpinnerAdapter(route.ratingAvr.toSortList(viewModel.filter.value))
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
        private const val ITEM_VIEW_TYPE_NOT_HERE    = 0x02
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is RouteItem.Filter -> ITEM_VIEW_TYPE_FILTER
            is RouteItem.LoadRoute -> ITEM_VIEW_TYPE_ROUTE
            else-> ITEM_VIEW_TYPE_NOT_HERE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE_FILTER -> FilterViewHolder(
                ItemRankingFilterBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false))
            ITEM_VIEW_TYPE_ROUTE -> RouteViewHolder(
                ItemRankingLinearBinding
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

    class OnClickListener(val clickListener: (route: Route)-> Unit){
        fun onClick(route: Route) = clickListener(route)
    }

}
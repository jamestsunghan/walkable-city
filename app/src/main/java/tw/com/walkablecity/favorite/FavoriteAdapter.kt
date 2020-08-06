package tw.com.walkablecity.favorite


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util.displaySliderValue
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.databinding.ItemFavoriteRouteFilterBinding
import tw.com.walkablecity.databinding.ItemFavoriteRouteLinearBinding
import tw.com.walkablecity.ext.toSortList
import tw.com.walkablecity.loadroute.route.CharacterSpinnerAdapter
import tw.com.walkablecity.loadroute.route.RouteItem

class FavoriteAdapter(private val viewModel: FavoriteViewModel) :
    ListAdapter<RouteItem, RecyclerView.ViewHolder>(DiffCallback) {

    class FilterViewHolder(private val binding: ItemFavoriteRouteFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: FavoriteViewModel) {

            binding.viewModel = viewModel

            binding.minuteText =
                displaySliderValue(binding.timeSlider.values, binding.timeSlider.valueTo)

            binding.timeSlider.addOnChangeListener { slider, _, _ ->
                viewModel.setTimeFilter(slider.values, slider.valueTo)
                viewModel.timeFilter(slider.values, slider.valueTo, viewModel.filter.value)

                binding.minuteText = displaySliderValue(slider.values, slider.valueTo)
            }
            binding.executePendingBindings()
        }
    }

    class RouteViewHolder(private val binding: ItemFavoriteRouteLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(route: Route, viewModel: FavoriteViewModel) {

            Logger.d("sortList ${route.ratingAvr.toSortList(viewModel.filter.value)} ratingAvr ${route.ratingAvr}")
            binding.characterSpinner.adapter =
                CharacterSpinnerAdapter(route.ratingAvr.toSortList(viewModel.filter.value))
            binding.route = route
            binding.root.setOnClickListener {
                viewModel.select(route)
            }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<RouteItem>() {
        override fun areItemsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: RouteItem, newItem: RouteItem): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TYPE_FILTER = 0x00
        private const val ITEM_VIEW_TYPE_ROUTE = 0x01

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RouteItem.Filter -> ITEM_VIEW_TYPE_FILTER
            is RouteItem.LoadRoute -> ITEM_VIEW_TYPE_ROUTE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_FILTER -> FilterViewHolder(
                ItemFavoriteRouteFilterBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
            ITEM_VIEW_TYPE_ROUTE -> RouteViewHolder(
                ItemFavoriteRouteLinearBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RouteViewHolder -> holder.bind(
                (getItem(position) as RouteItem.LoadRoute).route,
                viewModel
            )
            is FilterViewHolder -> holder.bind(viewModel)
        }
    }


}
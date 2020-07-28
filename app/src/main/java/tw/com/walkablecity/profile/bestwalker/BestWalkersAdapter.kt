package tw.com.walkablecity.profile.bestwalker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.data.User
import tw.com.walkablecity.databinding.ItemWalkersChampionBinding
import tw.com.walkablecity.databinding.ItemWalkersTheRestBinding
import tw.com.walkablecity.databinding.ItemWalkersTop3Binding
import java.lang.IllegalArgumentException

class BestWalkersAdapter(val viewModel: BestWalkersViewModel): ListAdapter<WalkerItem, RecyclerView.ViewHolder>(DiffCallback) {

    class ChampionViewHolder(private val binding: ItemWalkersChampionBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(champ: User, viewModel: BestWalkersViewModel){
            binding.champ = champ
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    class Top3ViewHolder(private val binding: ItemWalkersTop3Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(top3: List<User>, viewModel: BestWalkersViewModel){
            binding.first = top3[0]
            binding.second = top3[1]
            binding.third = top3[2]
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    class WalkersViewHolder(private val binding: ItemWalkersTheRestBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(walker: User, rankingDisplay: Int, viewModel: BestWalkersViewModel){
            binding.walker = walker
            binding.position = rankingDisplay
            binding.viewModel = viewModel
            binding.user = UserManager.user
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<WalkerItem>(){
        override fun areItemsTheSame(oldItem: WalkerItem, newItem: WalkerItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: WalkerItem, newItem: WalkerItem): Boolean {
            return oldItem.id == newItem.id
        }
        private const val ITEM_VIEW_TYPE_CHAMP      = 0x00
        private const val ITEM_VIEW_TYPE_TOPS       = 0x01
        private const val ITEM_VIEW_TYPE_WALKERS    = 0x02
    }

    override fun getItemViewType(position: Int): Int {
        return when(val item = getItem(position)){
                is WalkerItem.Tops    -> {
                    if(item.tops.size == 1) ITEM_VIEW_TYPE_CHAMP
                    else ITEM_VIEW_TYPE_TOPS
                }
                is WalkerItem.Walkers -> ITEM_VIEW_TYPE_WALKERS

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){

            ITEM_VIEW_TYPE_CHAMP  -> ChampionViewHolder(ItemWalkersChampionBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            ITEM_VIEW_TYPE_TOPS   -> Top3ViewHolder(ItemWalkersTop3Binding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            ITEM_VIEW_TYPE_WALKERS-> WalkersViewHolder(ItemWalkersTheRestBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))

            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val rankingDisplay = if(itemCount>3)position else position + 1
        when(holder){
            is ChampionViewHolder ->holder.bind((getItem(position) as WalkerItem.Tops).tops[0], viewModel)
            is Top3ViewHolder     ->holder.bind((getItem(position) as WalkerItem.Tops).tops, viewModel)
            is WalkersViewHolder  ->holder.bind((getItem(position) as WalkerItem.Walkers).walker, rankingDisplay, viewModel)
        }
    }
}

sealed class WalkerItem{

    abstract val id: String?

    data class Tops(val tops: List<User>): WalkerItem(){
        override val id: String?
            get() = tops.joinToString { it.idCustom.toString() }
    }

    data class Walkers(val walker: User): WalkerItem(){
        override val id: String?
            get() = walker.id
    }


}

package tw.com.walkablecity.eventdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.data.FriendListWrapper
import tw.com.walkablecity.databinding.ItemEventDetailBoardFqBinding

class FrequencyAdapter(val viewModel: EventDetailViewModel): ListAdapter<FriendListWrapper, FrequencyAdapter.FrequencyViewHolder>(DiffCallback) {

    class FrequencyViewHolder(private val binding: ItemEventDetailBoardFqBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(list: FriendListWrapper, position: Int, viewModel: EventDetailViewModel){
            binding.friendList = list
            binding.position = position + 1
            binding.viewModel = viewModel
            binding.recyclerFq.adapter = FrequencyFriendAdapter(viewModel)
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<FriendListWrapper>(){
        override fun areItemsTheSame(oldItem: FriendListWrapper, newItem: FriendListWrapper): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FriendListWrapper, newItem: FriendListWrapper): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrequencyViewHolder {
        return FrequencyViewHolder(ItemEventDetailBoardFqBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FrequencyViewHolder, position: Int) {
        holder.bind(getItem(position), position, viewModel)
    }
}
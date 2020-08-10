package tw.com.walkablecity.host.add2event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.databinding.ItemAddFriend2EventHorizontalBinding

class AddListAdapter : ListAdapter<Friend, AddListAdapter.AddListViewHolder>(DiffCallback) {

    class AddListViewHolder(private val binding: ItemAddFriend2EventHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: Friend) {
            binding.friend = friend
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddListViewHolder {
        return AddListViewHolder(
            ItemAddFriend2EventHorizontalBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AddListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
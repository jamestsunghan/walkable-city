package tw.com.walkablecity.host.add2event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.databinding.ItemAddFriend2EventBinding
import tw.com.walkablecity.host.HostViewModel

class AddFriend2EventAdapter(val viewModel: HostViewModel) :
    ListAdapter<Friend, AddFriend2EventAdapter.FriendViewHolder>(DiffCallback) {

    class FriendViewHolder(private val binding: ItemAddFriend2EventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: Friend, viewModel: HostViewModel) {
            binding.friend = friend

            binding.friendCheckbox.isChecked = viewModel.addList.value?.contains(friend) ?: false

            binding.friendCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.addFriendToAddList(friend)
                } else {
                    viewModel.removeFriendToAddList(friend)
                }
            }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            ItemAddFriend2EventBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel)
    }
}
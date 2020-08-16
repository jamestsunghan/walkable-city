package tw.com.walkablecity.eventdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.databinding.ItemEventDetailFrequencyFriendBinding

class FrequencyFriendAdapter(private val viewModel: EventDetailViewModel) :
    ListAdapter<Friend, FrequencyFriendAdapter.FriendViewHolder>(DiffCallback) {

    class FriendViewHolder(private val binding: ItemEventDetailFrequencyFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend, position: Int, viewModel: EventDetailViewModel) {
            binding.friend = friend
            binding.position = position + 1
            binding.isAccomplished =
                friend.accomplish ?: 0f >=
                        (viewModel.event.target?.hour?.times(60 * 60)) ?: requireNotNull(
                    viewModel.event.target?.distance
                )
            val accomplishDisplay = if (viewModel.event.target?.hour == null) {
                String.format(
                    getString(
                        R.string.walker_km
                    ), friend.accomplish ?: 0f
                )
            } else {
                String.format(
                    getString(
                        R.string.walker_hour
                    ), friend.accomplish?.div(60 * 60) ?: 0f
                )
            }
            binding.display = accomplishDisplay
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

        private const val TOP = 3
    }

    override fun getItemCount(): Int {

        return if (super.getItemCount() > TOP) {
            TOP
        } else {
            super.getItemCount()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            ItemEventDetailFrequencyFriendBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position), position, viewModel)
    }
}
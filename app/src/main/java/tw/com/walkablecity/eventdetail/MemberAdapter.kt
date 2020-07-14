package tw.com.walkablecity.eventdetail

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.databinding.ItemMemberEventDetailBinding

class MemberAdapter(val viewModel: EventDetailViewModel): ListAdapter<Friend, MemberAdapter.MemberViewHolder>(DiffCallback) {
    class MemberViewHolder(private val binding: ItemMemberEventDetailBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(friend: Friend, viewModel: EventDetailViewModel, position: Int){
            binding.friend = friend
            binding.friendBar.backgroundTintList = ColorStateList.valueOf(viewModel.typeColor)
            binding.viewModel = viewModel
            binding.width = binding.friendBar.width
            Log.d("JJ","friend bar width ${binding.friendBar.width}")
            binding.position = position
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Friend>(){
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return MemberViewHolder(ItemMemberEventDetailBinding
            .inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel, position)
    }
}
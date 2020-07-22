package tw.com.walkablecity.eventdetail

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.databinding.ItemEventDetailBoardBinding
import tw.com.walkablecity.databinding.ItemMemberEventDetailBinding
import tw.com.walkablecity.ext.toFriend

class MemberAdapter(val viewModel: EventDetailViewModel): ListAdapter<MemberItem, RecyclerView.ViewHolder>(DiffCallback) {

    class BoardViewHolder(private val binding: ItemEventDetailBoardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(viewModel: EventDetailViewModel){
            binding.circleAccomplish.apply{
                setCircleColor(Util.getColor(R.color.grey_transparent))
                setStrokeWidth(20f)
            }
            binding.viewModel = viewModel
            binding.total = viewModel.circleList.value?.sum()?.times(100)
        }
    }

    class MemberViewHolder(private val binding: ItemMemberEventDetailBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(friend: Friend, viewModel: EventDetailViewModel, position: Int){
            binding.friend = friend
            binding.viewModel = viewModel
            binding.width = binding.friendBar.width
//            binding.friendSeekBar.apply{
//                max = viewModel.event.target?.distance?.times(1000)?.toInt() ?: requireNotNull(viewModel.event.target?.hour?.times(60*60)?.toInt())
//                progress = if(viewModel.event.target?.distance == null){
//                    friend.accomplish?.toInt() ?: 0
//                }else{
//                    friend.accomplish?.times(1000)?.toInt() ?: 0
//                }
//                Log.d("JJ_seekbar","max $max progress $progress")
//                isActivated = false
//                secondaryProgress = viewModel.typeColor
//            }
            binding.friendBarProgress.width =  friend.accomplish?.let{
                it.times(binding.friendBar.width).div(viewModel.event.target?.distance ?: requireNotNull(viewModel.event.target?.hour)*3600).toInt()
            } ?: binding.friendBar.width

            binding.user = UserManager.user?.toFriend()
//            Log.d("JJ","friend bar width ${binding.friendBar.width}")
//            Log.d("JJ","friend bar progress width ${binding.friendBarProgress.width}")
            binding.position = position
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<MemberItem>(){
        override fun areItemsTheSame(oldItem: MemberItem, newItem: MemberItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MemberItem, newItem: MemberItem): Boolean {
            return oldItem == newItem
        }
        private const val ITEM_VIEW_TYPE_BOARD     = 0x00
        private const val ITEM_VIEW_TYPE_MEMBER       = 0x01
    }

    override fun getItemViewType(position: Int): Int {
        return when(val item = getItem(position)){
            is MemberItem.Board -> ITEM_VIEW_TYPE_BOARD
            is MemberItem.Member -> ITEM_VIEW_TYPE_MEMBER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE_BOARD -> BoardViewHolder(ItemEventDetailBoardBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
            ITEM_VIEW_TYPE_MEMBER -> MemberViewHolder(ItemMemberEventDetailBinding
                .inflate(LayoutInflater.from(parent.context),parent,false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is BoardViewHolder ->holder.bind(viewModel)
            is MemberViewHolder ->holder.bind((getItem(position) as MemberItem.Member).member, viewModel, position)
        }
    }
}

sealed class MemberItem{
    data class Member(val member: Friend): MemberItem(){

    }
    object Board: MemberItem(){

    }
}
package tw.com.walkablecity.eventdetail

import android.content.res.ColorStateList
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.*
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.data.FriendListWrapper
import tw.com.walkablecity.data.MissionFQ
import tw.com.walkablecity.databinding.ItemEventDetailBoardBinding
import tw.com.walkablecity.databinding.ItemMemberEventDetailBinding
import tw.com.walkablecity.ext.toFriend
import java.text.SimpleDateFormat
import java.util.*

class MemberAdapter(val viewModel: EventDetailViewModel): ListAdapter<MemberItem, RecyclerView.ViewHolder>(DiffCallback) {

    class BoardViewHolder(private val binding: ItemEventDetailBoardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(viewModel: EventDetailViewModel, champ: Friend){
            binding.circleAccomplish.apply{
                setCircleColor(Util.getColor(R.color.grey_transparent))
                setStrokeWidth(20f)
            }
            binding.viewModel = viewModel
            binding.champ = champ.apply {
                accomplish = if(viewModel.event.type == EventType.HOUR_RACE){
                    accomplish?.div(60*60) ?: 0f
                } else{
                    accomplish
                }
            }
            binding.total = viewModel.circleList.value?.sum()?.times(100)
            binding.recyclerFq.adapter = FrequencyAdapter(viewModel)
            binding.recyclerFq.onFlingListener = null
            PagerSnapHelper().attachToRecyclerView(binding.recyclerFq)

        }
    }

    class MemberViewHolder(private val binding: ItemMemberEventDetailBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(friend: Friend, viewModel: EventDetailViewModel, position: Int){
            binding.friend = friend
            binding.viewModel = viewModel
            binding.isAccomplished = (friend.accomplish ?: 0f) >= (viewModel.event.target?.hour?.times(60*60) ?: requireNotNull(viewModel.event.target?.distance))
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
            val progressWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180f, WalkableApp.instance.resources.displayMetrics)
            binding.guideline.setGuidelineEnd(progressWidth.toInt())

            binding.friendBarProgress.width =  friend.accomplish?.let{
                it.times(progressWidth-binding.friendBar.marginEnd)
                    .div(viewModel.event.target?.distance ?: requireNotNull(viewModel.event.target?.hour)*3600).toInt()
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
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MemberItem, newItem: MemberItem): Boolean {
            return oldItem.id == newItem.id
        }
        private const val ITEM_VIEW_TYPE_BOARD     = 0x00
        private const val ITEM_VIEW_TYPE_MEMBER    = 0x01
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
            is BoardViewHolder ->holder.bind(viewModel, (getItem(1) as MemberItem.Member).member)
            is MemberViewHolder ->holder.bind((getItem(position) as MemberItem.Member).member, viewModel, position)
        }
    }
}

sealed class MemberItem{
    abstract val id : String
    data class Member(val member: Friend): MemberItem(){
        override val id: String
            get() = requireNotNull(member.id)
    }
    object Board: MemberItem(){
        override val id: String
            get() = "this is a book"
    }
}
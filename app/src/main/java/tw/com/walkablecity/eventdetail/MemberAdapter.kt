package tw.com.walkablecity.eventdetail

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import tw.com.walkablecity.*
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.databinding.ItemEventDetailBoardBinding
import tw.com.walkablecity.databinding.ItemMemberEventDetailBinding
import tw.com.walkablecity.detail.DetailCircleAdapter
import tw.com.walkablecity.util.Util

class MemberAdapter(val viewModel: EventDetailViewModel) :
    ListAdapter<MemberItem, RecyclerView.ViewHolder>(DiffCallback) {

    private lateinit var context: Context

    class BoardViewHolder(private val binding: ItemEventDetailBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: EventDetailViewModel, champ: Friend, context: Context) {

            binding.circleAccomplish.apply {
                setCircleColor(Util.getColor(R.color.grey_transparent))
                setStrokeWidth(20f)
            }

            binding.viewModel = viewModel

            viewModel.champ.observe(context as MainActivity, Observer {
                it?.let { champion ->
                    binding.champ = champion
                    binding.champAccomplished = if (viewModel.event.type == EventType.HOUR_RACE) {
                        champion.accomplish?.div(60 * 60) ?: 0f
                    } else {
                        champion.accomplish
                    }
                }
            })

            binding.total = viewModel.circleList.value?.sum()?.times(100)


            binding.recyclerFq.adapter = FrequencyAdapter(viewModel)

            binding.recyclerCircleFq.adapter = DetailCircleAdapter()

            binding.recyclerFq.onFlingListener = null

            val params = binding.recyclerCircleFq.layoutParams

            viewModel.listOfList.observe(context as MainActivity, Observer{
                it?.let{list->
                    if (list.size < 5){
                        params.width = RecyclerView.LayoutParams.WRAP_CONTENT
                    } else {
                        params.width = 0
                    }
                    binding.recyclerCircleFq.layoutParams = params
                }
            })

            val circleManager = binding.recyclerCircleFq.layoutManager

            binding.startShown = circleManager?.findViewByPosition(0)?.isShown ?: false

            binding.endShown = circleManager?.findViewByPosition(
                binding.recyclerCircleFq.adapter?.itemCount ?: 0
            )?.isShown ?: false

            val linearSnapHelper = LinearSnapHelper().apply {
                attachToRecyclerView(binding.recyclerFq)
            }

            binding.recyclerFq.setOnScrollChangeListener { _, _, _, _, _ ->
                viewModel.onGalleryScrollChange(binding.recyclerFq.layoutManager, linearSnapHelper)

                binding.startShown = circleManager?.findViewByPosition(0)?.isShown ?: false

                binding.endShown = circleManager?.findViewByPosition(
                    binding.recyclerCircleFq.adapter?.itemCount?.minus(1) ?: 0
                )?.isShown ?: false
            }
            viewModel.snapPosition.observe(context as MainActivity, Observer {
                (binding.recyclerCircleFq.adapter as DetailCircleAdapter).selectedPosition.value =
                    it
                binding.recyclerCircleFq.smoothScrollToPosition(it)


            })
        }
    }

    class MemberViewHolder(private val binding: ItemMemberEventDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend, viewModel: EventDetailViewModel, position: Int) {
            binding.friend = friend
            binding.viewModel = viewModel
            binding.isAccomplished =
                (friend.accomplish ?: 0f) >= (viewModel.event.target?.hour?.times(60 * 60)
                    ?: requireNotNull(viewModel.event.target?.distance))
            binding.width = binding.friendBar.width

            val progressWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                180f,
                WalkableApp.instance.resources.displayMetrics
            )
            binding.guideline.setGuidelineEnd(progressWidth.toInt())

            binding.friendBarProgress.width = friend.accomplish?.let {
                if (viewModel.event.target?.frequencyType == null) {
                    it.times(progressWidth - binding.friendBar.marginEnd)
                        .div(
                            viewModel.event.target?.distance
                                ?: requireNotNull(viewModel.event.target?.hour) * 3600
                        ).toInt()
                } else {
                    it.times(progressWidth - binding.friendBar.marginEnd)
                        .div(
                            viewModel.event.target.distance
                                ?: requireNotNull(viewModel.event.target.hour)
                        ).toInt()
                }

            } ?: binding.friendBar.width

            binding.user = UserManager.user?.toFriend()

            binding.position = position
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MemberItem>() {
        override fun areItemsTheSame(oldItem: MemberItem, newItem: MemberItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MemberItem, newItem: MemberItem): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_BOARD = 0x00
        private const val ITEM_VIEW_TYPE_MEMBER = 0x01
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MemberItem.Board -> ITEM_VIEW_TYPE_BOARD
            is MemberItem.Member -> ITEM_VIEW_TYPE_MEMBER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            ITEM_VIEW_TYPE_BOARD -> BoardViewHolder(
                ItemEventDetailBoardBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
            ITEM_VIEW_TYPE_MEMBER -> MemberViewHolder(
                ItemMemberEventDetailBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BoardViewHolder -> holder.bind(
                viewModel,
                (getItem(1) as MemberItem.Member).member,
                context
            )
            is MemberViewHolder -> holder.bind(
                (getItem(position) as MemberItem.Member).member,
                viewModel,
                position
            )
        }
    }
}

sealed class MemberItem {
    abstract val id: String

    data class Member(val member: Friend) : MemberItem() {
        override val id: String
            get() = requireNotNull(member.id)
    }

    object Board : MemberItem() {
        override val id: String
            get() = "this is a book"
    }
}
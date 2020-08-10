package tw.com.walkablecity.event.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Event
import tw.com.walkablecity.databinding.ItemEventItemGridBinding

class EventItemAdapter(private val viewModel: EventItemViewModel)
    : ListAdapter<Event, EventItemAdapter.EventItemViewHolder>(DiffCallback) {

    class EventItemViewHolder(private val binding: ItemEventItemGridBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(event: Event, viewModel: EventItemViewModel){
            binding.event = event
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Event>(){
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventItemViewHolder {
        return EventItemViewHolder(ItemEventItemGridBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: EventItemViewHolder, position: Int) {
        holder.bind(getItem(position),viewModel)
    }
}
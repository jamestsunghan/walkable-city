package tw.com.walkablecity.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.Comment
import tw.com.walkablecity.databinding.ItemDetailCommentBinding

class CommentAdapter: ListAdapter<Comment, CommentAdapter.CommentViewHolder>(DiffCallback) {
    class CommentViewHolder(private val binding: ItemDetailCommentBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(comment: Comment){
            binding.comment = comment
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Comment>(){
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(ItemDetailCommentBinding
            .inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
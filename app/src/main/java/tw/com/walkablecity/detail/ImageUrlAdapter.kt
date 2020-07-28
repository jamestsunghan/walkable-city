package tw.com.walkablecity.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.databinding.ItemDetailImageUrlBinding

class ImageUrlAdapter: ListAdapter<String, ImageUrlAdapter.ImageUrlViewHolder>(DiffCallback) {

    class ImageUrlViewHolder(private val binding: ItemDetailImageUrlBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(imageUrl: String){
            binding.imageUrl = imageUrl
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<String>(){

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageUrlViewHolder {
        return ImageUrlViewHolder(ItemDetailImageUrlBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageUrlViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
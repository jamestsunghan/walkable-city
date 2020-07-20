package tw.com.walkablecity.rating.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.PhotoPoint
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.databinding.ItemRatingPhotoPointBinding
import tw.com.walkablecity.rating.RatingType
import java.io.File

class RatingItemPhotoAdapter(val route: Route?, val type: RatingType): ListAdapter<PhotoPoint, RatingItemPhotoAdapter.PhotoViewHolder>(DiffCallback) {

    class PhotoViewHolder(private val binding: ItemRatingPhotoPointBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(photoPoint: PhotoPoint, type: RatingType){


            binding.photoPoint = photoPoint
            binding.reference = photoPoint.photo
            binding.type = requireNotNull(type)
            binding.executePendingBindings()

        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<PhotoPoint>(){
        override fun areItemsTheSame(oldItem: PhotoPoint, newItem: PhotoPoint): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoPoint, newItem: PhotoPoint): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(ItemRatingPhotoPointBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position), type)
    }
}
package tw.com.walkablecity.detail

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.MainActivity
import tw.com.walkablecity.databinding.ItemDetailCircleBinding

class DetailCircleAdapter: RecyclerView.Adapter<DetailCircleAdapter.ImageViewHolder>() {

    private lateinit var context: Context
    private var count = 0
    var selectedPosition = MutableLiveData<Int>()

    class ImageViewHolder(private val binding: ItemDetailCircleBinding): RecyclerView.ViewHolder(binding.root){

        var isSelected = MutableLiveData<Boolean>()

        fun bind(context: Context, selectedPosition: MutableLiveData<Int>){
            selectedPosition.observe(context as MainActivity, Observer{
                Log.d("JJ_snap", "selectedPosition ${it ?: -1}")
                binding.isSelected = it == adapterPosition
                binding.executePendingBindings()
            })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        return ImageViewHolder(ItemDetailCircleBinding
            .inflate(LayoutInflater.from(parent.context), parent , false))
    }

    override fun getItemCount(): Int {
        return count
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(context, selectedPosition)
    }

    fun submitCount(count: Int){
        this.count = count
        notifyDataSetChanged()
    }
}
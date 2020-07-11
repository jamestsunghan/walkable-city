package tw.com.walkablecity.host

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import tw.com.walkablecity.databinding.ItemEventTypeSpinnerBinding

class EventTypeSpinnerAdapter(private val strings:List<String>): BaseAdapter() {

    @Suppress("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ItemEventTypeSpinnerBinding
            .inflate(LayoutInflater.from(parent?.context), parent, false)
        binding.title = strings[position]
        binding.position = position
        return binding.root
    }

    override fun getItem(position: Int): Any {
        return strings[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return strings.size
    }
}
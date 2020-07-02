package tw.com.walkablecity.loadroute.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import tw.com.walkablecity.databinding.ItemCharacterSpinnerBinding


class CharacterSpinnerAdapter(private val strings: List<String>): BaseAdapter() {

    @Suppress("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ItemCharacterSpinnerBinding
            .inflate(LayoutInflater.from(parent?.context), parent, false)
        binding.title = strings[position]
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
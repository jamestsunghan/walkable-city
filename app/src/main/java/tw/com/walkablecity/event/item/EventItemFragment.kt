package tw.com.walkablecity.event.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentEventItemBinding
import tw.com.walkablecity.event.EventAdapter
import tw.com.walkablecity.event.EventPageType
import tw.com.walkablecity.ext.getVMFactory

class EventItemFragment(val type: EventPageType): Fragment() {

    val viewModel: EventItemViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentEventItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_event_item, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel







        return binding.root
    }
}
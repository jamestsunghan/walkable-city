package tw.com.walkablecity.event.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentEventItemBinding
import tw.com.walkablecity.event.EventFragmentDirections
import tw.com.walkablecity.event.EventPageType
import tw.com.walkablecity.ext.getVMFactory

class EventItemFragment(val type: EventPageType): Fragment() {

    val viewModel: EventItemViewModel by viewModels{getVMFactory(type)}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentEventItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_event_item, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerEventItem.adapter = EventItemAdapter(viewModel)

        viewModel.navigateToEventDetail.observe(viewLifecycleOwner, Observer{
            it?.let{event->
                findNavController().navigate(EventFragmentDirections.actionGlobalEventDetailFragment(event))
                viewModel.navigateToDetailComplete()
            }
        })

        viewModel.eventAllList.observe(viewLifecycleOwner, Observer{
            it?.let{eventList->
                viewModel.getEventListToFilter(eventList)
            }
        })

        return binding.root
    }
}
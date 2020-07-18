package tw.com.walkablecity.event


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentEventBinding
import tw.com.walkablecity.ext.getVMFactory

class EventFragment : Fragment() {


    private val viewModel: EventViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentEventBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_event, container, false)
        binding.lifecycleOwner = this

        binding.viewpagerEvent.let{ pager->
            pager.adapter = EventAdapter(childFragmentManager)
            pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabsEvent))
        }

        binding.viewModel = viewModel

        viewModel.navigateToHost.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(EventFragmentDirections.actionEventFragmentToHostFragment(null))
                viewModel.navigateToHostComplete()
            }
        })

        return binding.root
    }



}

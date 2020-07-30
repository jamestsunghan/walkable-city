package tw.com.walkablecity.event


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import tw.com.walkablecity.Logger
import tw.com.walkablecity.MainViewModel

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.databinding.FragmentEventBinding
import tw.com.walkablecity.ext.getVMFactory

class EventFragment : Fragment() {


    private val viewModel: EventViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        UserManager.user?.id?.let{
            mainViewModel.getInvitation(it)
        }

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
                findNavController().navigate(EventFragmentDirections.actionEventFragmentToHostFragment())
                viewModel.navigateToHostComplete()
            }
        })

        val tab = binding.tabsEvent.getTabAt(0)
        val badge = tab?.orCreateBadge
        

        Logger.d("does badge exist? ${badge ?: "null"} number ${badge?.number} ")

        return binding.root
    }



}

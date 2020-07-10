package tw.com.walkablecity.host


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentHostBinding
import tw.com.walkablecity.ext.getVMFactory

class HostFragment : DialogFragment() {


    private val viewModel: HostViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHostBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_host, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.navigateToEvents.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(HostFragmentDirections.actionGlobalEventFragment())
                viewModel.navigateToEventsComplete()
            }
        })

        return binding.root
    }


}

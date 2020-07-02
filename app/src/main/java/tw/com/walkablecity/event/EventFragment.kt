package tw.com.walkablecity.event


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

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

        binding.viewModel = viewModel

        return binding.root
    }



}

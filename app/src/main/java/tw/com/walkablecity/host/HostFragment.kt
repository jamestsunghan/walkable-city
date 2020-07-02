package tw.com.walkablecity.host


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentHostBinding
import tw.com.walkablecity.ext.getVMFactory

class HostFragment : Fragment() {


    private val viewModel: HostViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHostBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_host, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }


}

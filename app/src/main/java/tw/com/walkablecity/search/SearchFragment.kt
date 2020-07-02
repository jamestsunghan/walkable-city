package tw.com.walkablecity.search


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentSearchBinding
import tw.com.walkablecity.ext.getVMFactory

class SearchFragment : DialogFragment() {


    private val viewModel: SearchViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSearchBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }


}

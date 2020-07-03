package tw.com.walkablecity.search


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentSearchBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.HomeFragmentDirections

class SearchFragment : DialogFragment() {


    private val viewModel: SearchViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSearchBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this

        binding.timerSlider.addOnChangeListener { slider, value, fromUser ->
            viewModel.range.value = slider.values
        }

        viewModel.range.observe(viewLifecycleOwner, Observer {
            it?.let{
                Log.d("JJ", "list float $it")
            }
        })

        viewModel.selectedRoute.observe(viewLifecycleOwner, Observer {
            it?.let{
                findNavController().navigate(SearchFragmentDirections.actionGlobalHomeFragment(it))
                viewModel.searchComplete()
            }
        })

        binding.viewModel = viewModel



        return binding.root
    }


}

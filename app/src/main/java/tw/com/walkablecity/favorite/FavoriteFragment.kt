package tw.com.walkablecity.favorite


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import tw.com.walkablecity.util.Logger

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentFavoriteBinding
import tw.com.walkablecity.ext.getVMFactory

class FavoriteFragment : Fragment() {


    private val viewModel: FavoriteViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentFavoriteBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_favorite, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = FavoriteAdapter(viewModel)
        binding.recyclerRouteItem.adapter = adapter

        viewModel.filter.observe(viewLifecycleOwner, Observer {
            it?.let{
                Logger.d("route sorting ${it.text}")
                viewModel.timeFilter(viewModel.routeTime.value ?: listOf(Float.MIN_VALUE, Float.MAX_VALUE), viewModel.sliderMax.value ?: Float.MAX_VALUE , it)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.selectRoute.observe(viewLifecycleOwner, Observer {
            it?.let{

                findNavController().navigate(FavoriteFragmentDirections.actionGlobalDetailFragment(it))
                viewModel.navigationComplete()
            }
        })


        return binding.root
    }
    

}

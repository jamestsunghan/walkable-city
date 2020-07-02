package tw.com.walkablecity.loadroute.route


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import tw.com.walkablecity.NavigationDirections

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentRouteItemBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.loadroute.LoadRouteFragmentDirections
import tw.com.walkablecity.loadroute.LoadRouteType

class RouteItemFragment(private val loadRouteType: LoadRouteType) : Fragment() {


    private val viewModel: RouteItemViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRouteItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_route_item, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = RouteItemAdapter(viewModel)
        binding.recyclerRouteItem.adapter = adapter

        viewModel.filter.observe(viewLifecycleOwner, Observer {
            it?.let{
                Log.d("JJ","route sorting ${it.text}")
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.selectRoute.observe(viewLifecycleOwner, Observer {
            it?.let{

                findNavController().navigate(LoadRouteFragmentDirections.actionGlobalHomeFragment(it))
                viewModel.navigationComplete()
            }
        })

        return binding.root
    }


}

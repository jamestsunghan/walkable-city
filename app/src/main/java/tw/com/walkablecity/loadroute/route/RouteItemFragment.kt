package tw.com.walkablecity.loadroute.route


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
import tw.com.walkablecity.databinding.FragmentRouteItemBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.loadroute.LoadRouteFragmentDirections
import tw.com.walkablecity.loadroute.LoadRouteType

class RouteItemFragment(private val loadRouteType: LoadRouteType) : Fragment() {


    private val viewModel: RouteItemViewModel by viewModels { getVMFactory(loadRouteType) }

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
            it?.let {
                Logger.d("route sorting ${it.text}")
                viewModel.timeFilter(
                    viewModel.routeTime.value ?: listOf(Float.MIN_VALUE, Float.MAX_VALUE)
                    , viewModel.sliderMax.value ?: Float.MAX_VALUE, it
                )
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.selectRoute.observe(viewLifecycleOwner, Observer {
            it?.let {

                findNavController().navigate(
                    LoadRouteFragmentDirections.actionGlobalHomeFragment(it, null)
                )
                viewModel.navigationComplete()
            }
        })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    LoadRouteFragmentDirections.actionGlobalDetailFragment(it)
                )
                viewModel.navigateToDetailComplete()
            }
        })


        return binding.root
    }


}

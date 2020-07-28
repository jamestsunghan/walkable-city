package tw.com.walkablecity.ranking


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import tw.com.walkablecity.Logger

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentRankingBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.loadroute.LoadRouteFragmentDirections

class RankingFragment : Fragment() {


    private val viewModel: RankingViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRankingBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_ranking, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = RankingAdapter(viewModel)
        binding.recyclerRouteItem.adapter = adapter

        viewModel.filter.observe(viewLifecycleOwner, Observer {
            it?.let{
                Logger.d("route sorting ${it.text}")
                viewModel.routeSorting(it, adapter)
            }
        })

        viewModel.selectRoute.observe(viewLifecycleOwner, Observer {
            it?.let{

                findNavController().navigate(RankingFragmentDirections.actionGlobalDetailFragment(it))
                viewModel.navigationComplete()
            }
        })

        viewModel.routeTime.observe(viewLifecycleOwner, Observer {
            it?.let{
                viewModel.timeFilter(it)
            }
        })

        return binding.root
    }


}

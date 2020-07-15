package tw.com.walkablecity.rating


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentRatingBinding
import tw.com.walkablecity.ext.getVMFactory

class RatingFragment : Fragment() {

    val viewModel: RatingViewModel by viewModels{getVMFactory(RatingFragmentArgs.fromBundle(requireArguments()).selectedRoute
        , RatingFragmentArgs.fromBundle(requireArguments()).walkKey, null)}



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRatingBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_rating, container, false)
        binding.lifecycleOwner = this

        childFragmentManager.setFragmentResultListener("navigation", viewLifecycleOwner){requestKey, bundle ->
            val result = bundle.getInt("sent")
            viewModel.navigateToSearch.value = viewModel.navigateToSearch.value?.plus(result) ?:1
        }


        val adapter = RatingAdapter(childFragmentManager, viewModel)

        binding.viewpagerRating.let{
            it.adapter = adapter
            it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabsRating))
        }

        viewModel.navigateToSearch.observe(viewLifecycleOwner, Observer{
            it?.let{
                if(it > 1){
                    findNavController().navigate(RatingFragmentDirections.actionGlobalHomeFragment(null, null))
                    viewModel.sendComplete()
                }
            }
        })

        return binding.root
    }


}

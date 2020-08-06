package tw.com.walkablecity.rating


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    val viewModel: RatingViewModel by viewModels { getVMFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRatingBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_rating, container, false)
        binding.lifecycleOwner = this

        childFragmentManager.setFragmentResultListener(
            REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getInt(BUNDLE_KEY)
            viewModel.addDonePageCount(result)
        }

        val selectedRoute = RatingFragmentArgs.fromBundle(requireArguments()).selectedRoute
        val walk = RatingFragmentArgs.fromBundle(requireArguments()).walkKey

        val adapter = RatingAdapter(
            childFragmentManager, selectedRoute, walk
            , RatingFragmentArgs.fromBundle(requireArguments()).willCreateKey
            , RatingFragmentArgs.fromBundle(requireArguments()).photoPointsKey?.toList()
        )

        binding.viewpagerRating.let {
            it.adapter = adapter
            it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabsRating))
        }

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            it?.let {count->
                if (count > 1) {
                    findNavController().navigate(
                        RatingFragmentDirections.actionGlobalHomeFragment(
                            null,
                            null
                        )
                    )
                    viewModel.sendComplete()
                }
            }
        })

        return binding.root
    }

    companion object{
        const val REQUEST_KEY = "navigation"
        const val BUNDLE_KEY = "sent"
    }
}

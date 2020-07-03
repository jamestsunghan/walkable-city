package tw.com.walkablecity.rating


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentRatingBinding
import tw.com.walkablecity.ext.getVMFactory

class RatingFragment : Fragment() {

    private val viewModel: RatingViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRatingBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_rating, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.routeRating.observe(viewLifecycleOwner, Observer {
            it?.let{
                findNavController().navigate(RatingFragmentDirections.actionGlobalHomeFragment(null))
                viewModel.sendComplete()
            }
        })



        return binding.root
    }


}

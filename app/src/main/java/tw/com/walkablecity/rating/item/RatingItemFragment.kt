package tw.com.walkablecity.rating.item


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*

import tw.com.walkablecity.R
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.databinding.FragmentRatingItemBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.rating.RatingFragment
import tw.com.walkablecity.rating.RatingFragmentArgs
import tw.com.walkablecity.rating.RatingFragmentDirections
import tw.com.walkablecity.rating.RatingType

class RatingItemFragment(private val type: RatingType, private val route: Route?, private val walk: Walk) : Fragment() {

    val viewModel: RatingItemViewModel by viewModels{
        getVMFactory(route, walk , type)}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRatingItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_rating_item, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.routeRating.observe(viewLifecycleOwner, Observer {
            it?.let{
                val result = 1
                this.setFragmentResult("navigation",bundleOf("sent" to result))
            }
        })







        return binding.root
    }



}

package tw.com.walkablecity.detail


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentDetailBinding
import tw.com.walkablecity.ext.getVMFactory

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModels{getVMFactory(DetailFragmentArgs.fromBundle(requireArguments()).routeKey)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDetailBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.route = viewModel.route
        binding.favIcon.isSelected = false

        binding.recyclerComment.adapter = CommentAdapter()
        binding.recyclerDetailUrl.adapter = ImageUrlAdapter()

        val linearSnapHelper = LinearSnapHelper().apply {
            attachToRecyclerView(binding.recyclerDetailUrl)
        }

//        binding.recyclerDetailUrl.setOnScrollChangeListener { _, _, _, _, _ ->
//            viewModel.onGalleryScrollChange(
//                binding.recyclerDetailUrl.layoutManager,
//                linearSnapHelper
//            )
//        }

        viewModel.favoriteAdded.observe(viewLifecycleOwner, Observer{
            it?.let{
                binding.favIcon.isSelected = it
            }
        })



        viewModel.navigatingToRanking.observe(viewLifecycleOwner, Observer {
            if(it){
                findNavController().navigateUp()
                viewModel.navigationComplete()
            }
        })

        viewModel.photoPoints.observe(viewLifecycleOwner, Observer{
            it?.let{
                viewModel.addMaptodisplayPhotos(it)
            }
        })

        return binding.root
    }


}

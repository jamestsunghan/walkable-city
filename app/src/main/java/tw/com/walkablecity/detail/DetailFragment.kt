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
import tw.com.walkablecity.util.Logger

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentDetailBinding
import tw.com.walkablecity.ext.getVMFactory

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModels{
        getVMFactory(DetailFragmentArgs.fromBundle(requireArguments()).routeKey)
    }

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

        binding.recyclerCircle.adapter = DetailCircleAdapter()

        val linearSnapHelper = LinearSnapHelper().apply {
            attachToRecyclerView(binding.recyclerDetailUrl)
        }

        binding.recyclerDetailUrl.setOnScrollChangeListener { _, _, _, _, _ ->
            viewModel.onGalleryScrollChange(binding.recyclerDetailUrl.layoutManager, linearSnapHelper)
        }

        viewModel.displayPhotos.observe(viewLifecycleOwner, Observer{

            binding.recyclerDetailUrl.scrollToPosition(0)

            viewModel.snapPosition.observe(viewLifecycleOwner, Observer {position->
                (binding.recyclerCircle.adapter as DetailCircleAdapter).selectedPosition.value = position
                Logger.d("JJ_snap snapPosition $position")
            })

        })

        viewModel.favoriteAdded.observe(viewLifecycleOwner, Observer{
            it?.let{added->
                binding.favIcon.isSelected = added
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer{
            it?.let{route->
                findNavController()
                    .navigate(DetailFragmentDirections.actionGlobalHomeFragment(route, null))
                viewModel.navigateToHomeComplete()
            }
        })

        viewModel.navigatingToRanking.observe(viewLifecycleOwner, Observer {confirmed->
            if(confirmed){
                findNavController().navigateUp()
                viewModel.navigationComplete()
            }
        })

        viewModel.photoPoints.observe(viewLifecycleOwner, Observer{
            it?.let{points->
                viewModel.addMaptodisplayPhotos(points)
            }
        })

        return binding.root
    }
}

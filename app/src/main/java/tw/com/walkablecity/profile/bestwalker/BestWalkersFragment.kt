package tw.com.walkablecity.profile.bestwalker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.data.FrequencyType
import tw.com.walkablecity.data.User
import tw.com.walkablecity.databinding.FragmentBestWalkersBinding
import tw.com.walkablecity.ext.getVMFactory

class BestWalkersFragment : Fragment() {

    val viewModel: BestWalkersViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentBestWalkersBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_best_walkers, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.recyclerWalker.adapter = BestWalkersAdapter(viewModel)

        viewModel.userFriendList.observe(viewLifecycleOwner, Observer{
            it?.let{list->

                viewModel.sortList(list, requireNotNull(viewModel.accumulationType.value))
            }
        })

        viewModel.accumulationType.observe(viewLifecycleOwner, Observer{
            it?.let{type->
                viewModel.userFriendList.value?.let{friend->
                    viewModel.sortList(friend,type)
                }
            }
        })

        return binding.root
    }


}

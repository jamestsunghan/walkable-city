package tw.com.walkablecity.host.add2event


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import tw.com.walkablecity.util.Logger

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentAddFriend2EventBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.host.HostViewModel

class AddFriend2EventFragment : Fragment() {

    private val viewModel: HostViewModel by navGraphViewModels(R.id.navigation2) {
        getVMFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAddFriend2EventBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_add_friend_2_event, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = AddFriend2EventAdapter(viewModel)

        val adapterAddList = AddListAdapter()

        binding.recyclerFriend.adapter = adapter

        binding.recyclerAddList.adapter = adapterAddList

        viewModel.friendList.observe(viewLifecycleOwner, Observer {
            it?.let {list->
                Logger.d("friendlist $list")
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.addList.observe(viewLifecycleOwner, Observer {
            it?.let {list->
                Logger.d("addList $list")
                adapterAddList.notifyDataSetChanged()
            }
        })


        viewModel.navigateToHost.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    AddFriend2EventFragmentDirections
                        .actionAddFriend2EventFragmentToHostFragment()
                )
                viewModel.friendSelectedComplete()

            }
        })

        return binding.root
    }
}

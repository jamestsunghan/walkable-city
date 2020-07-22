package tw.com.walkablecity.host.add2event


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentAddFriend2EventBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.host.HostViewModel

class AddFriend2EventFragment : Fragment() {


//    private val viewModel: AddFriend2EventViewModel by viewModels{ getVMFactory( AddFriend2EventFragmentArgs.fromBundle(requireArguments()).friendListKey?.toList()) }

    private val viewModel: HostViewModel by navGraphViewModels(R.id.navigation2){
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
            it?.let{
                Log.d("JJ", "friendlist $it")
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.addList.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.d("JJ","addList $it")
                adapterAddList.notifyDataSetChanged()
            }
        })


        viewModel.navigateToHost.observe(viewLifecycleOwner, Observer{
            it?.let{
                findNavController().navigate(AddFriend2EventFragmentDirections
                    .actionAddFriend2EventFragmentToHostFragment())
                viewModel.friendSelectedComplete()

            }
        })


        return binding.root
    }



}

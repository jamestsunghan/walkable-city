package tw.com.walkablecity.eventdetail


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import tw.com.walkablecity.*

import tw.com.walkablecity.databinding.FragmentEventDetailBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.util.Logger

class EventDetailFragment : Fragment() {

    val viewModel: EventDetailViewModel by viewModels {
        getVMFactory(
            EventDetailFragmentArgs.fromBundle(requireArguments()).eventKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val binding: FragmentEventDetailBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_event_detail, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.user = UserManager.user

        binding.friend = UserManager.user?.toFriend()

        binding.isAdded = viewModel.event.member.find {member->
            member.id == UserManager.user?.id
        } != null

        val adapter = MemberAdapter(viewModel, viewLifecycleOwner)
        binding.recyclerMember.adapter = adapter

        binding.maybeLater.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.walkResultSingle.observe(viewLifecycleOwner, Observer {
            it?.let {result->
                viewModel.addToWalkResult(result)
            }
        })

        viewModel.walkResult.observe(viewLifecycleOwner, Observer {
            it?.let { list ->

                Logger.d("list size ${list.size}")
                Logger.d("list $list")
                viewModel.keepGettingWalkResult(list)
                Logger.d("count ${viewModel.resultCount}")

            }
        })

        viewModel.joinSuccess.observe(viewLifecycleOwner, Observer {
            it?.let {joined->

                if (joined) {
                    mainViewModel.getUserEventCount(requireNotNull(UserManager.user?.id))
                    Logger.d("badge event dialog from detail")
                    findNavController().navigate(EventDetailFragmentDirections.actionGlobalEventFragment())
                }
            }
        })

        viewModel.circleList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.notifyDataSetChanged()
            }
        })
        
        viewModel.listOfList.observe(viewLifecycleOwner, Observer{yeah->
            yeah?.let{list->
                Logger.d("size ${list.size} list $list")
                for(item in 0 until list.size){
                    Logger.d("list of list ${list[item].data.map{it.accomplish}}")
                }
            }
        })

        return binding.root
    }
}

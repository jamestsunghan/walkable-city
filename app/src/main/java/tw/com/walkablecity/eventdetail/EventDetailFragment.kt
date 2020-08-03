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

        binding.isAdded = viewModel.event.member.find {
            it.id == UserManager.user?.id
        } != null

        val adapter = MemberAdapter(viewModel)
        binding.recyclerMember.adapter = adapter

//        binding.recyclerMember.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener{
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//        })

        binding.maybeLater.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.walkResultSingle.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.addToWalkResult(it)
            }
        })

        viewModel.walkResult.observe(viewLifecycleOwner, Observer {
            it?.let { list ->

                Logger.d("list size ${list.size}")
                Logger.d("list $list")
                viewModel.resultCount += 1

                Logger.d("count ${viewModel.resultCount}")


                if (viewModel.resultCount == viewModel.listMemberId.size) {

                    viewModel.eventMember.value?.mapIndexed { index, friend ->
                        requireNotNull(viewModel.eventMember.value)[index].accomplish = list[index]
                        friend
                    }
                    viewModel.sortByAccomplish()
                    viewModel.circleList.value = list.sortedByDescending { f -> f }.map { fa ->
                        fa.div(
                            viewModel.event.target?.distance
                                ?: requireNotNull(viewModel.event.target?.hour) * 60 * 60
                        )
                    }
//                    adapter.notifyDataSetChanged()
                } else if (viewModel.resultCount > viewModel.listMemberId.size) {
                    viewModel.resultCount = 0
//                    adapter.notifyDataSetChanged()
                } else {
                    viewModel.getMemberWalkResult(
                        requireNotNull(viewModel.event.startDate)
                        , requireNotNull(viewModel.event.target), viewModel.listMemberId
                    )

                }
            }
        })

        viewModel.joinSuccess.observe(viewLifecycleOwner, Observer {
            it?.let {

                if (it) {
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


//        viewModel.timerText.observe(viewLifecycleOwner, Observer{
//            it?.let{
//                Logger.d("JJ_time time $it")
//                binding.timerText = it
//            }
//        })
//        viewModel.listOfList.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                Logger.d("JJ_list list of list ${it.size}")
//            }
//        })


        return binding.root
    }
}

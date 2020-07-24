package tw.com.walkablecity.eventdetail


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.getColor
import tw.com.walkablecity.Util.lessThenTenPadStart
import tw.com.walkablecity.databinding.FragmentEventDetailBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.ext.toFriend

class EventDetailFragment : Fragment() {



    val viewModel: EventDetailViewModel by viewModels{getVMFactory(EventDetailFragmentArgs.fromBundle(requireArguments()).eventKey)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentEventDetailBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_event_detail, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.user = UserManager.user

        binding.friend = UserManager.user?.toFriend()

        binding.isAdded = viewModel.event.member.find{
            it.id == UserManager.user?.id
        }!= null

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

        viewModel.walkResultSingle.observe(viewLifecycleOwner, Observer{
            it?.let{
                viewModel.addToWalkResult(it)
            }
        })

        viewModel.walkResult.observe(viewLifecycleOwner, Observer{
            it?.let{ list ->

                Log.d("JJ", "list size ${list.size}")
                Log.d("JJ", "list $list")
                viewModel.resultCount += 1

                Log.d("JJ","count ${viewModel.resultCount}")


                if(viewModel.resultCount == viewModel.listMemberId.size) {

                    viewModel.eventMember.value?.mapIndexed { index, friend ->
                        requireNotNull(viewModel.eventMember.value)[index].accomplish = list[index]
                        friend
                    }
                    viewModel.sortByAccomplish()
                    viewModel.circleList.value = list.sortedByDescending { f->f }.map{ fa-> fa.div(viewModel.event.target?.distance ?: requireNotNull(viewModel.event.target?.hour)*60*60) }
//                    adapter.notifyDataSetChanged()
                }else if(viewModel.resultCount > viewModel.listMemberId.size){
                    viewModel.resultCount = 0
//                    adapter.notifyDataSetChanged()
                }else{
                    viewModel.getMemberWalkResult(requireNotNull(viewModel.event.startDate)
                        , requireNotNull(viewModel.event.target) ,viewModel.listMemberId)

                }
            }
        })

        viewModel.joinSuccess.observe(viewLifecycleOwner, Observer{
            it?.let{

                if(it){
                    findNavController().navigate(EventDetailFragmentDirections.actionGlobalEventFragment())
                }
            }
        })

        viewModel.circleList.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.notifyDataSetChanged()
            }
        })




//        viewModel.timerText.observe(viewLifecycleOwner, Observer{
//            it?.let{
//                Log.d("JJ_time","time $it")
//                binding.timerText = it
//            }
//        })
        viewModel.listOfList.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.d("JJ_list", "list of list ${it.size}")
            }
        })


        return binding.root
    }


    companion object{
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val ONE_DAY = 24 * 60 * 60 * ONE_SECOND
    }
}

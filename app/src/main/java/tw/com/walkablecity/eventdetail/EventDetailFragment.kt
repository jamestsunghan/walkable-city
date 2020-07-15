package tw.com.walkablecity.eventdetail


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.Timestamp

import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.lessThenTenPadStart
import tw.com.walkablecity.databinding.FragmentEventDetailBinding
import tw.com.walkablecity.ext.getVMFactory

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

        val adapter = MemberAdapter(viewModel)
        binding.recyclerMember.adapter = adapter

        viewModel.walkResultSingle.observe(viewLifecycleOwner, Observer{
            it?.let{
                viewModel.addToWalkResult(it)
            }
        })

        viewModel.walkResult.observe(viewLifecycleOwner, Observer{
            it?.let{
//                binding.recyclerMember.adapter = MemberAdapter(viewModel)
                Log.d("JJ", "list size ${it.size}")
                Log.d("JJ", "list $it")
                viewModel.resultCount += 1

                Log.d("JJ","count ${viewModel.resultCount}")


                if(viewModel.resultCount == viewModel.listMemberId.size) {

                    viewModel.eventMember.value?.mapIndexed { index, friend ->
                        requireNotNull(viewModel.eventMember.value)[index].accomplish = it[index]
                        friend
                    }

                    viewModel.sortByAccomplish()
                    adapter.notifyDataSetChanged()
                }else if(viewModel.resultCount > viewModel.listMemberId.size){
                    viewModel.resultCount = 0
                    adapter.notifyDataSetChanged()
                }else{
                    viewModel.getMemberWalkResult(requireNotNull(viewModel.event.startDate)
                        , requireNotNull(viewModel.event.target) ,viewModel.listMemberId)

                }
            }
        })




//        viewModel.timerText.observe(viewLifecycleOwner, Observer{
//            it?.let{
//                Log.d("JJ_time","time $it")
//                binding.timerText = it
//            }
//        })



        return binding.root
    }


    companion object{
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val ONE_DAY = 24 * 60 * 60 * ONE_SECOND
    }
}

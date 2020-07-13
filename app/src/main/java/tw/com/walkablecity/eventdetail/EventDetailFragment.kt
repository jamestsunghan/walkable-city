package tw.com.walkablecity.eventdetail


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import tw.com.walkablecity.R
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

        binding.recyclerMember.adapter = MemberAdapter(viewModel)

        return binding.root
    }


}

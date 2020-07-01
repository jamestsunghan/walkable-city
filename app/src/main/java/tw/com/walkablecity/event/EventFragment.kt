package tw.com.walkablecity.event


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentEventBinding

class EventFragment : Fragment() {

    private lateinit var viewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentEventBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_event, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        return binding.root
    }



}

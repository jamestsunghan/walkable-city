package tw.com.walkablecity.profile.badge


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentBadgeBinding

class BadgeFragment : Fragment() {


    private lateinit var viewModel: BadgeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentBadgeBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_badge, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(BadgeViewModel::class.java)

        return binding.root
    }


}

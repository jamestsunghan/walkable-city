package tw.com.walkablecity.profile.explore

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {


    private lateinit var viewModel: ExploreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentExploreBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_explore, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)

        binding.viewModel = viewModel

        return binding.root


    }


}

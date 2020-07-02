package tw.com.walkablecity.ranking


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentRankingBinding
import tw.com.walkablecity.ext.getVMFactory

class RankingFragment : Fragment() {


    private val viewModel: RankingViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRankingBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_ranking, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }


}

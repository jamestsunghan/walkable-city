package tw.com.walkablecity.ranking


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {


    private lateinit var viewModel: RankingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRankingBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_ranking, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(RankingViewModel::class.java)

        return binding.root
    }


}

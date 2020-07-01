package tw.com.walkablecity.profile.bestwalker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentBestWalkersBinding

class BestWalkersFragment : Fragment() {

    private lateinit var viewModel: BestWalkersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentBestWalkersBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_best_walkers, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(BestWalkersViewModel::class.java)

        return binding.root
    }


}

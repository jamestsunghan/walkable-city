package tw.com.walkablecity.favorite


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentFavoriteBinding
import tw.com.walkablecity.ext.getVMFactory

class FavoriteFragment : Fragment() {


    private val viewModel: FavoriteViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentFavoriteBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_favorite, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        return binding.root
    }
    

}

package tw.com.walkablecity.favorite


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {


    private lateinit var viewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentFavoriteBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_favorite, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)


        return binding.root
    }
    

}

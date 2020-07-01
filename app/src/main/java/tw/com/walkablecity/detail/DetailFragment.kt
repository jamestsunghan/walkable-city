package tw.com.walkablecity.detail


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDetailBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        return binding.root
    }


}

package tw.com.walkablecity.search

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentSearchBinding

class SearchFragment : DialogFragment() {


    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSearchBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        binding.viewModel = viewModel

        return binding.root
    }


}

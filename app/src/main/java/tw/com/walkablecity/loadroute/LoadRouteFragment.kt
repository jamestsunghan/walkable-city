package tw.com.walkablecity.loadroute

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentLoadRouteBinding

class LoadRouteFragment : Fragment() {

    private lateinit var viewModel: LoadRouteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoadRouteBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_load_route, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(LoadRouteViewModel::class.java)

        return binding.root
    }


}

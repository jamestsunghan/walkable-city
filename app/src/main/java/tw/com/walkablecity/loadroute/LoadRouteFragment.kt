package tw.com.walkablecity.loadroute

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentLoadRouteBinding
import tw.com.walkablecity.ext.getVMFactory

class LoadRouteFragment : Fragment() {

    private val viewModel: LoadRouteViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoadRouteBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_load_route, container, false)
        binding.lifecycleOwner = this

        binding.viewpagerRoute.let{
            it.adapter = LoadRouteAdapter(childFragmentManager)
            it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabsLoadRoute))
        }

        binding.viewModel = viewModel

        return binding.root
    }


}

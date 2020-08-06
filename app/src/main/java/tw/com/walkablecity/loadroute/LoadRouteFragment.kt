package tw.com.walkablecity.loadroute

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentLoadRouteBinding

class LoadRouteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLoadRouteBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_load_route, container, false)

        binding.lifecycleOwner = this

        binding.viewpagerRoute.let { pager ->
            pager.adapter = LoadRouteAdapter(childFragmentManager)
            pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabsLoadRoute))
        }

        return binding.root
    }


}

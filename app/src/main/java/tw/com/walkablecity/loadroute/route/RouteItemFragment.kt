package tw.com.walkablecity.loadroute.route

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentRouteItemBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.loadroute.LoadRouteType

class RouteItemFragment(private val loadRouteType: LoadRouteType) : Fragment() {


    private val viewModel: RouteItemViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRouteItemBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_route_item, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerRouteItem.adapter = RouteItemAdapter()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

}

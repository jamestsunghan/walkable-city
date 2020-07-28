package tw.com.walkablecity.home.createroute

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentCreateRouteDialogBinding
import tw.com.walkablecity.ext.getVMFactory

class CreateRouteDialogFragment : DialogFragment() {


    private val viewModel: CreateRouteDialogViewModel by viewModels{ getVMFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCreateRouteDialogBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_create_route_dialog, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val selectRoute = CreateRouteDialogFragmentArgs.fromBundle(requireArguments()).selectedRoute
        val walk = CreateRouteDialogFragmentArgs.fromBundle(requireArguments()).walkKey
        val photoPoints = CreateRouteDialogFragmentArgs.fromBundle(requireArguments()).photoPoints


        viewModel.navigateToRating.observe(viewLifecycleOwner, Observer {
            if(it){
                findNavController().navigate(CreateRouteDialogFragmentDirections
                    .actionCreateRouteDialogFragmentToRatingFragment(
                        selectRoute, walk, requireNotNull(viewModel.willCreate.value)
                        , photoPoints))
                viewModel.navigationComplete()
            }
        })


        return binding.root
    }


}

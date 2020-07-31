package tw.com.walkablecity.home.destroywalk

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentDestroyWalkDialogBinding

class DestroyWalkDialogFragment : DialogFragment() {


    private lateinit var viewModel: DestroyWalkDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDestroyWalkDialogBinding = DataBindingUtil
            .inflate(inflater,R.layout.fragment_destroy_walk_dialog, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(DestroyWalkDialogViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.destroyAll.observe(viewLifecycleOwner, Observer {
//            it?.let{destroyConfirmed->
//                if(destroyConfirmed){
//
//                }
//
//            }
        })

        return binding.root
    }



}

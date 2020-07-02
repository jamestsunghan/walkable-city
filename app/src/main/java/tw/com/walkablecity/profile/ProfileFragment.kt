package tw.com.walkablecity.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentProfileBinding
import tw.com.walkablecity.ext.getVMFactory

class ProfileFragment : Fragment() {


    private val viewModel: ProfileViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentProfileBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel



        return binding.root
    }


}

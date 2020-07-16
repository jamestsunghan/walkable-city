package tw.com.walkablecity.profile.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.databinding.FragmentSettingsBinding
import tw.com.walkablecity.ext.getVMFactory

class SettingsFragment : Fragment() {


    private val viewModel: SettingsViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSettingsBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = this



        binding.viewModel = viewModel

        binding.user = UserManager.user

        return binding.root
    }

}

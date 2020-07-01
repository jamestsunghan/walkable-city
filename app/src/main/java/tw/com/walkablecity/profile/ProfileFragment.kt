package tw.com.walkablecity.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {


    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentProfileBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)



        return binding.root
    }


}

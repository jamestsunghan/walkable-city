package tw.com.walkablecity.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
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
        binding.user = UserManager.user
        Log.d("JJ", " accumulate hours ${UserManager.user?.accumulatedHour?.total}")

        viewModel.navigateToAddFriend.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(ProfileFragmentDirections.actionGlobalAddFriendFragment())
                viewModel.navigateToAddFriendComplete()
            }
        })

        return binding.root
    }


}

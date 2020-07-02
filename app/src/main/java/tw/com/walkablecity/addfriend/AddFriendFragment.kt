package tw.com.walkablecity.addfriend


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import tw.com.walkablecity.R
import tw.com.walkablecity.databinding.FragmentAddFriendBinding
import tw.com.walkablecity.ext.getVMFactory

class AddFriendFragment : Fragment() {

    private val viewModel: AddFriendViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAddFriendBinding = DataBindingUtil
            .inflate(inflater,R.layout.fragment_add_friend, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }



}

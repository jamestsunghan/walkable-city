package tw.com.walkablecity.addfriend


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.MainViewModel

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.databinding.FragmentAddFriendBinding
import tw.com.walkablecity.ext.getVMFactory
import java.util.*

class AddFriendFragment : Fragment() {

    private val viewModel: AddFriendViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val binding: FragmentAddFriendBinding = DataBindingUtil
            .inflate(inflater,R.layout.fragment_add_friend, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.editLoginId.setEndIconOnClickListener {

            viewModel.checkFriendAdded(viewModel.idSearch.value)

        }

        viewModel.alreadyFriend.observe(viewLifecycleOwner, Observer{
            it?.let{
                if(it){
                    makeShortToast(R.string.already_friend)
                }else{
                    viewModel.searchFriendWithId(viewModel.idSearch.value)
                }
            }
        })

        viewModel.friendToAdd.observe(viewLifecycleOwner, Observer{
            binding.friend = it
        })

        viewModel.friendAdded.observe(viewLifecycleOwner, Observer{
            it?.let{
                if(it){
                    makeShortToast(R.string.add_success)
                    UserManager.user?.id?.let{id->
                        mainViewModel.getUserFriendCount(id)
                    }

                    viewModel.resetAddFriend()
                }else{
                    makeShortToast(R.string.add_failed)
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer{
            it?.let{
                Toast.makeText(this.requireContext(),it,Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }



}

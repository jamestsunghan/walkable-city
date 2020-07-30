package tw.com.walkablecity.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import tw.com.walkablecity.*

import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.databinding.FragmentProfileBinding
import tw.com.walkablecity.event.EventFragmentDirections
import tw.com.walkablecity.ext.getVMFactory

class ProfileFragment : Fragment() {


    private val viewModel: ProfileViewModel by viewModels{getVMFactory()}




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        UserManager.user?.id?.let{id->
            mainViewModel.getUserFriendCount(id)
        }

        val binding: FragmentProfileBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        binding.user = UserManager.user
        Logger.d( " accumulate hours ${UserManager.user?.accumulatedHour?.total}")

        viewModel.navigateToAddFriend.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(ProfileFragmentDirections.actionGlobalAddFriendFragment())
                viewModel.navigateToAddFriendComplete()
            }
        })

        viewModel.navigateToSetting.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsFragment())
                viewModel.navigateToSettingComplete()
            }
        })

        viewModel.navigateToWalkers.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBestWalkersFragment())
                viewModel.navigateToWalkersComplete()
            }
        })

        viewModel.navigateToBadge.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBadgeFragment())
                viewModel.navigateToBadgeComplete()
            }
        })

        viewModel.navigateToExplorer.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToExploreFragment())
                viewModel.navigateToExplorerComplete()
            }
        })

        mainViewModel.friendCount.observe(viewLifecycleOwner, Observer{
            it?.let{count->
                viewModel.setUpgrade(count, Util.getIntFromSP(BadgeType.FRIEND_COUNT.key))
            }
        })

        viewModel.upgrade.observe(viewLifecycleOwner, Observer {
            it?.let{grade->
                Logger.d("let see some grade $grade")
                if(grade > 0){
                    val dialog = AlertDialog.Builder(requireContext())
                        .setMessage("您有 $grade 個新徽章歐! 快到散步徽章看看!")
                        .setPositiveButton("前往") { dialog, which ->
                            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBadgeFragment())
                        }.setNegativeButton("稍後再說"){dialog, which ->
                            dialog.cancel()
                        }
                    dialog.show()
                }
            }
        })


        return binding.root
    }


}

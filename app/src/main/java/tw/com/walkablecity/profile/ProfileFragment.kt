package tw.com.walkablecity.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import tw.com.walkablecity.*
import tw.com.walkablecity.util.Util.showBadgeDialog

import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.databinding.FragmentProfileBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels { getVMFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val binding: FragmentProfileBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_profile, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.user = UserManager.user

        Logger.d(" accumulate hours ${UserManager.user?.accumulatedHour?.total}")

        viewModel.navigateToAddFriend.observe(viewLifecycleOwner, Observer {confirmed->
            if (confirmed) {
                findNavController()
                    .navigate(ProfileFragmentDirections.actionGlobalAddFriendFragment())
                viewModel.navigateToAddFriendComplete()
            }
        })

        viewModel.navigateToSetting.observe(viewLifecycleOwner, Observer {confirmed->
            if (confirmed) {
                findNavController()
                    .navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsFragment())
                viewModel.navigateToSettingComplete()
            }
        })

        viewModel.navigateToWalkers.observe(viewLifecycleOwner, Observer {confirmed->
            if (confirmed) {
                findNavController()
                    .navigate(ProfileFragmentDirections.actionProfileFragmentToBestWalkersFragment())
                viewModel.navigateToWalkersComplete()
            }
        })

        viewModel.navigateToBadge.observe(viewLifecycleOwner, Observer {confirmed->
            if (confirmed) {
                findNavController()
                    .navigate(ProfileFragmentDirections.actionProfileFragmentToBadgeFragment())
                viewModel.navigateToBadgeComplete()
            }
        })

        viewModel.navigateToExplorer.observe(viewLifecycleOwner, Observer {confirmed->
            if (confirmed) {
                findNavController()
                    .navigate(ProfileFragmentDirections.actionProfileFragmentToExploreFragment())
                viewModel.navigateToExplorerComplete()
            }
        })

        mainViewModel.friendCount.observe(viewLifecycleOwner, Observer {
            it?.let { count ->
                viewModel.setUpgrade(count, Util.getIntFromSP(BadgeType.FRIEND_COUNT.key))
            }
        })

        var previousUpgrade = 0
        viewModel.upgrade.observe(viewLifecycleOwner, Observer {
            it?.let { grade ->
                Logger.d("let see some grade $grade")
                if (grade > previousUpgrade) {
                    mainViewModel.addToBadgeTotal(grade, R.id.profileFragment)
                    val dialog = showBadgeDialog(
                        grade, requireContext(), findNavController(),
                        ProfileFragmentDirections.actionProfileFragmentToBadgeFragment()
                        , getString(R.string.badge_dialog_friend)
                    )

                    dialog.show()
                    previousUpgrade = grade
                }
            }
        })

        return binding.root
    }
}

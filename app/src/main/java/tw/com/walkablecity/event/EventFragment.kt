package tw.com.walkablecity.event


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
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import tw.com.walkablecity.Logger
import tw.com.walkablecity.MainViewModel

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.getColor
import tw.com.walkablecity.Util.getIntFromSP
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.databinding.FragmentEventBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.HomeFragmentDirections

class EventFragment : Fragment() {


    private val viewModel: EventViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        UserManager.user?.id?.let{
            mainViewModel.getInvitation(it)
            mainViewModel.getUserEventCount(it)
        }

        val binding: FragmentEventBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_event, container, false)
        binding.lifecycleOwner = this

        binding.viewpagerEvent.adapter = EventAdapter2(requireActivity())

        val mediator = TabLayoutMediator(binding.tabsEvent, binding.viewpagerEvent,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = EventPageType.values()[position].title
                if(position == 2){
                    tab.orCreateBadge.apply {
                        backgroundColor = getColor(R.color.red_heart_c73e3a)
                        number = mainViewModel.invitation.value ?: 0
                        isVisible = number > 0
                        badgeGravity = BadgeDrawable.TOP_END
                    }
                }
            })
        mediator.attach()

        mainViewModel.invitation.observe(viewLifecycleOwner, Observer{
            it?.let{
                binding.tabsEvent.getTabAt(2)?.orCreateBadge?.apply {
                    number = it
                }
            }
        })

        binding.viewModel = viewModel

        viewModel.navigateToHost.observe(viewLifecycleOwner, Observer{
            if(it){
                findNavController().navigate(EventFragmentDirections.actionEventFragmentToHostFragment())
                viewModel.navigateToHostComplete()
            }
        })

        mainViewModel.eventCount.observe(viewLifecycleOwner, Observer{
            it?.let{count->
                viewModel.setUpgrade(count, getIntFromSP(BadgeType.EVENT_COUNT.key))
            }
        })

        viewModel.upgrade.observe(viewLifecycleOwner, Observer{
            it?.let{grade->
                Logger.d("let see some grade $grade")
                if(grade > 0){
                    mainViewModel.addToBadgeTotal(grade, R.id.eventFragment)
                    val dialog = AlertDialog.Builder(requireContext())
                        .setMessage("您有 $grade 個新徽章歐! 快到散步徽章看看!")
                        .setPositiveButton("前往") { dialog, which ->
                            findNavController().navigate(EventFragmentDirections.actionGlobalBadgeFragment())
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

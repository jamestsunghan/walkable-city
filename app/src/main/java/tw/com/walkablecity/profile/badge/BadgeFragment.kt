package tw.com.walkablecity.profile.badge


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.MainViewModel

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Util.putDataToSharedPreference
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.databinding.FragmentBadgeBinding
import tw.com.walkablecity.ext.getVMFactory


class BadgeFragment : Fragment() {

    private val viewModel: BadgeViewModel by viewModels { getVMFactory() }

    lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val binding: FragmentBadgeBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_badge, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.user = UserManager.user

        binding.activity = requireActivity()

        viewModel.eventCount.observe(viewLifecycleOwner, Observer {
            it?.let {count->
                Logger.d("eventCount $count")
            }
        })

        viewModel.friendCount.observe(viewLifecycleOwner, Observer {
            it?.let {count->
                Logger.d("friendCount $count")
            }
        })

        return binding.root
    }

    override fun onDestroyView() {

        putDataToSharedPreference(
            BadgeType.ACCU_HOUR.key,
            UserManager.user?.accumulatedHour?.total ?: 0f
        )

        putDataToSharedPreference(
            BadgeType.ACCU_KM.key,
            UserManager.user?.accumulatedKm?.total ?: 0f
        )

        viewModel.friendCount.value?.let {count->
            putDataToSharedPreference(BadgeType.FRIEND_COUNT.key, count = count)
        }

        viewModel.eventCount.value?.let {count->
            putDataToSharedPreference(BadgeType.EVENT_COUNT.key, count = count)
        }

        mainViewModel.resetBadgeTotal()

        super.onDestroyView()
    }
}

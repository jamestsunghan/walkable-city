package tw.com.walkablecity.profile.badge


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingMethod
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.Logger

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.databinding.FragmentBadgeBinding
import tw.com.walkablecity.ext.getVMFactory


class BadgeFragment : Fragment() {


    private val viewModel: BadgeViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentBadgeBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_badge, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.user = UserManager.user

        binding.activity = requireActivity()

        viewModel.eventCount.observe(viewLifecycleOwner, Observer{
            it?.let{
                Logger.d( "eventCount $it")
            }
        })

        viewModel.friendCount.observe(viewLifecycleOwner, Observer{
            it?.let{
                Logger.d("friendCount $it")
            }
        })

        return binding.root
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        val x = intent?.extras
    }
}

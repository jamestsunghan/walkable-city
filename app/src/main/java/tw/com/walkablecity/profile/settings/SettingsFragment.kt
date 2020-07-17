package tw.com.walkablecity.profile.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.makeShortToast
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

        binding.afterMealSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.afterMealSwitch(isChecked)
        }

        binding.goodWeatherSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.goodWeatherSwitchOn(isChecked)
        }

        binding.viewModel = viewModel

        binding.user = UserManager.user

        viewModel.notifyAfterMeal.observe(viewLifecycleOwner, Observer {
            it?.let{isChecked->
                if(isChecked) makeShortToast(R.string.after_meal_on)
                else makeShortToast(R.string.after_meal_off)
            }
        })

        viewModel.notifyGoodWeather.observe(viewLifecycleOwner, Observer {
            it?.let{isChecked->
                if(isChecked) makeShortToast(R.string.good_weather_on)
                else makeShortToast(R.string.good_weather_off)
            }
        })

        return binding.root
    }

}

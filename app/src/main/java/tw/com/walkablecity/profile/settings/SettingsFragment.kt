package tw.com.walkablecity.profile.settings


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.databinding.FragmentSettingsBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.HomeFragment

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

        binding.goodWeatherSwitch.setOnClickListener {
            checkPermission()
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
                if(isChecked){
                    makeShortToast(R.string.good_weather_on)
                }
                else{
                    makeShortToast(R.string.good_weather_off)
                }
            }
        })

        viewModel.currentLocation.observe(viewLifecycleOwner, Observer{
            it?.let{
                binding.goodWeatherSwitch.isChecked = true
                viewModel.getWeather(it)
            }
        })

        viewModel.weatherResult.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.d("JJ_weather", "weather result $it ")
            }
        })



        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode != HomeFragment.REQUEST_LOCATION) return
        if(Util.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ){
            viewModel.permissionGranted()
            viewModel.clientCurrentLocation()
        }else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                viewModel.permissionDeniedForever()
            }
            viewModel.permissionDenied()
        }


    }

    fun checkPermission(){
        if(ContextCompat.checkSelfPermission(
                WalkableApp.instance,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){
            viewModel.permissionGranted()
            viewModel.clientCurrentLocation()
        }else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                viewModel.permissionDeniedForever()
            }
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
//            requestPermission(requireActivity() as MainActivity, REQUEST_LOCATION
//                , Manifest.permission.ACCESS_FINE_LOCATION,true)
        }



    }

    companion object{
        const val REQUEST_LOCATION      = 0x00
    }

}

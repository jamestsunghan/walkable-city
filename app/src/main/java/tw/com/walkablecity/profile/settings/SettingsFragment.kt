package tw.com.walkablecity.profile.settings


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.WorkManager
import tw.com.walkablecity.*

import tw.com.walkablecity.Util.lessThenTenPadStart
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.databinding.FragmentSettingsBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {


    private val viewModel: SettingsViewModel by viewModels{getVMFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSettingsBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = this
        binding.goodWeatherSwitch.isChecked = UserManager.user?.weather ?: false
        binding.afterMealSwitch.isChecked = UserManager.user?.meal ?: false

        binding.afterMealSwitch.setOnClickListener {
            viewModel.updateMealNotification(binding.afterMealSwitch.isChecked, requireNotNull(UserManager.user?.id))
        }

        binding.afterMealSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.afterMealSwitch(isChecked)
        }

        binding.goodWeatherSwitch.setOnClickListener {
            Logger.d("JJ_weather good weather checked ${binding.goodWeatherSwitch.isChecked}")
            if(binding.goodWeatherSwitch.isChecked) checkPermission(binding.goodWeatherSwitch.isChecked)
            else viewModel.updateWeatherNotification(binding.goodWeatherSwitch.isChecked, requireNotNull(UserManager.user?.id))
        }

        binding.goodWeatherSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.goodWeatherSwitchOn(isChecked)
        }



        binding.viewModel = viewModel

        binding.user = UserManager.user

        viewModel.weatherActivated.observe(viewLifecycleOwner, Observer{
            it?.let{isActivated->
                WalkableApp.instance.getWeather(isActivated)
                binding.goodWeatherSwitch.isChecked = isActivated
                UserManager.user?.weather = isActivated
            }
        })

        viewModel.mealActivated.observe(viewLifecycleOwner, Observer{
            it?.let{isActivated->
                WalkableApp.instance.notifyAfterMeal(isActivated)
                binding.afterMealSwitch.isChecked = isActivated
                UserManager.user?.meal = isActivated

            }
        })

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
//                viewModel.getWeather(it)
            }
        })

//        viewModel.weatherResult.observe(viewLifecycleOwner, Observer{
//            it?.let{weather->
//                Log.d("JJ_weather", "weather result $weather ")
//                val today = Calendar.getInstance()
//                val hourWalkable = weather.hourly.filter{item->
//                    item.feelsLike ?: 50f < 35f && item.feelsLike ?: 0f > 15f
//                }
//                    .filter{item->
//                        val itemDate = SimpleDateFormat("dd", Locale.TAIWAN).format(item.dt?.times(1000))
//                        val todayDate = lessThenTenPadStart(today.get(Calendar.DAY_OF_MONTH).toLong())
//                        Log.d("JJ_weather", "hour date $itemDate & today date $todayDate")
//                        itemDate == todayDate
//                }
//                for(item in hourWalkable){
//                    val hrDisplay = SimpleDateFormat("MM-dd HH:mm", Locale.TAIWAN).format(item.dt?.times(1000))
//                    Log.d("JJ_weather", "weather hour $hrDisplay feels like ${item.feelsLike} Celsius ")
//
//                }
//                val hourDisplay = weather.hourly.map{hour->
//                    SimpleDateFormat("hh:mm", Locale.TAIWAN).format(hour.dt)
//                }
//            }
//        })



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
            viewModel.updateWeatherNotification(true, requireNotNull(UserManager.user?.id))
        }else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                viewModel.permissionDeniedForever()
            }
            viewModel.permissionDenied()
        }


    }

    fun checkPermission(activate: Boolean){
        if(ContextCompat.checkSelfPermission(
                WalkableApp.instance,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){
            viewModel.permissionGranted()
            viewModel.updateWeatherNotification(activate, requireNotNull(UserManager.user?.id))
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

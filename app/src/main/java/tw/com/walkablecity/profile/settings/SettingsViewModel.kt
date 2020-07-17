package tw.com.walkablecity.profile.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.source.WalkableRepository

class SettingsViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _notifyAfterMeal = MutableLiveData<Boolean>(false)
    val notifyAfterMeal: LiveData<Boolean> get() = _notifyAfterMeal

    private val _notifyGoodWeather = MutableLiveData<Boolean>(false)
    val notifyGoodWeather: LiveData<Boolean> get() = _notifyGoodWeather


    fun afterMealSwitch(isChecked: Boolean){
        _notifyAfterMeal.value = isChecked
    }

    fun goodWeatherSwitchOn(isChecked: Boolean){
        _notifyGoodWeather.value = isChecked
    }



}

package tw.com.walkablecity.profile.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.source.WalkableRepository

class SettingsViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _notifyAfterMeal = MutableLiveData<Boolean>(UserManager.user?.meal)
    val notifyAfterMeal: LiveData<Boolean> get() = _notifyAfterMeal

    private val _notifyGoodWeather = MutableLiveData<Boolean>(UserManager.user?.weather)
    val notifyGoodWeather: LiveData<Boolean> get() = _notifyGoodWeather

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng> get() = _currentLocation

    private val _weatherActivated = MutableLiveData<Boolean>()
    val weatherActivated: LiveData<Boolean> get() = _weatherActivated

    private val _mealActivated = MutableLiveData<Boolean>()
    val mealActivated: LiveData<Boolean> get() = _mealActivated

    private val _permissionDenied =  MutableLiveData<Boolean>(false)
    val permissionDenied: LiveData<Boolean> get() = _permissionDenied

    private val _dontAskAgain = MutableLiveData<Boolean>(false)
    val dontAskAgain: LiveData<Boolean> get() = _dontAskAgain

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun afterMealSwitch(isChecked: Boolean){
        _notifyAfterMeal.value = isChecked
    }

    fun goodWeatherSwitchOn(isChecked: Boolean){
        _notifyGoodWeather.value = isChecked
    }

    fun permissionDeniedForever(){
        _dontAskAgain.value = true
    }

    fun permissionDenied(){
        _permissionDenied.value = true
    }

    fun permissionGranted(){
        _permissionDenied.value = false
    }

    fun updateWeatherNotification(activate: Boolean, userId: String){
        _status.value = LoadStatus.LOADING

        coroutineScope.launch {

            val result = walkableRepository.updateWeatherNotification(activate, userId)

            _weatherActivated.value = result.setLiveBoolean(_error, _status)


        }
    }

    fun updateMealNotification(activate: Boolean, userId: String){
        _status.value = LoadStatus.LOADING

        coroutineScope.launch {

            val result = walkableRepository.updateMealNotification(activate, userId)

            _mealActivated.value = result.setLiveBoolean(_error, _status)

        }
    }

}

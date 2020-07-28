package tw.com.walkablecity.profile.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.WeatherResult
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.WalkerStatus

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

    private val _weatherResult = MutableLiveData<WeatherResult>()
    val weatherResult: LiveData<WeatherResult> get() = _weatherResult

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

    fun clientCurrentLocation(){

        _status.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = walkableRepository.getUserCurrentLocation()

            _currentLocation.value = when(result){
                is Result.Success->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = Util.getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }
            }

        }

    }

    fun getWeather(latLng: LatLng){

        _status.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = walkableRepository.getWeather(latLng)

            _weatherResult.value = when(result){
                is Result.Success->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = Util.getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }
            }

        }



    }

    fun updateWeatherNotification(activate: Boolean, userId: String){
        _status.value = LoadStatus.LOADING

        coroutineScope.launch {

            val result = walkableRepository.updateWeatherNotification(activate, userId)

            _weatherActivated.value = when(result){

                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    false
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    false
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    false
                }
            }


        }
    }

    fun updateMealNotification(activate: Boolean, userId: String){
        _status.value = LoadStatus.LOADING

        coroutineScope.launch {

            val result = walkableRepository.updateMealNotification(activate, userId)

            _mealActivated.value = when(result){

                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    false
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    false
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    false
                }
            }


        }
    }




}

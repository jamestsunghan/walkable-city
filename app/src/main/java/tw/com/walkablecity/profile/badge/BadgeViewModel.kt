package tw.com.walkablecity.profile.badge

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.getAccumulatedFromSharedPreference
import tw.com.walkablecity.Util.getCountFromSharedPreference
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository

class BadgeViewModel(val walkableRepository: WalkableRepository) : ViewModel() {
    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _friendCount = MutableLiveData<Int>()
    val friendCount: LiveData<Int> get() = _friendCount

    private val _eventCount = MutableLiveData<Int>()
    val eventCount: LiveData<Int> get() = _eventCount

    private val _shareThisBadge = MutableLiveData<Boolean>()

    val accuHour = getAccumulatedFromSharedPreference(BadgeType.ACCU_HOUR.key
        , UserManager.user?.accumulatedHour?.total ?: 0f)

    val accuKm = getAccumulatedFromSharedPreference(BadgeType.ACCU_KM.key
        , UserManager.user?.accumulatedKm?.total ?: 0f)

    val sharedEventCount = WalkableApp.instance
        .getSharedPreferences(Util.BADGE_DATA, Context.MODE_PRIVATE).getInt(BadgeType.EVENT_COUNT.key, -1)
    val sharedFriendCount = WalkableApp.instance
        .getSharedPreferences(Util.BADGE_DATA, Context.MODE_PRIVATE).getInt(BadgeType.FRIEND_COUNT.key, -1)

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init{
        UserManager.user?.id?.let{
            getFriendCount(it)
            getEventCount(it)
        }

    }

    fun getFriendCount(userId: String){
        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            when(val result = walkableRepository.getUserFriendSimple(userId)){

                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    _friendCount.value = result.data.size
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                }
                else->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                }

            }

        }
    }

    fun getEventCount(userId: String){
        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            when(val result = walkableRepository.getUserEvents(userId)){

                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    _eventCount.value = result.data.size
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                }
                else->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                }

            }

        }
    }


}

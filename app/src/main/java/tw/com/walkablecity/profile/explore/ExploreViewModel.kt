package tw.com.walkablecity.profile.explore

import android.util.TypedValue
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.EventTarget
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toLatLng
import tw.com.walkablecity.host.HostViewModel

class ExploreViewModel(val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _userWalks = MutableLiveData<List<Walk>>()
    val userWalks: LiveData<List<Walk>>
        get() = _userWalks

    val currentLocation = MutableLiveData<LatLng>()

    val canDrawMap = MediatorLiveData<Boolean>().apply {

        addSource(userWalks) { list ->
            value = (list != null) && (currentLocation.value != null)
        }
        addSource(currentLocation) { latLng ->
            value = (userWalks.value != null) && (latLng != null)
        }
    }

    private val _permissionDenied = MutableLiveData<Boolean>(false)
    val permissionDenied: LiveData<Boolean>
        get() = _permissionDenied

    private val _dontAskAgain = MutableLiveData<Boolean>(false)
    val dontAskAgain: LiveData<Boolean>
        get() = _dontAskAgain

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val bitmapRealDimens = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, BITMAP_WIDTH,
        WalkableApp.instance.resources.displayMetrics
    ).toInt()
    private val drawable = WalkableApp.instance.resources.getDrawable(
        R.mipmap.ic_launcher_foot_in_white, WalkableApp.instance.theme
    )

    val bitmap = drawable.toBitmap(bitmapRealDimens, bitmapRealDimens)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        UserManager.user?.id?.let {
            getUserWalks(it)
        }
    }

    fun permissionDeniedForever() {
        _dontAskAgain.value = true
    }

    fun permissionDenied() {
        _permissionDenied.value = true
    }

    fun permissionGranted() {
        _permissionDenied.value = false
    }

    private fun getUserWalks(userId: String) {

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getUserWalks(userId)

            _userWalks.value = result.handleResultWith(_error, _status)

        }
    }

    fun clientCurrentLocation() {

        _status.value = LoadStatus.LOADING

        coroutineScope.launch {
            val result = walkableRepository.getUserCurrentLocation()

            currentLocation.value = result.handleResultWith(_error, _status)?.toLatLng()

        }
    }

    companion object {
        private const val BITMAP_WIDTH = 25f
    }
}

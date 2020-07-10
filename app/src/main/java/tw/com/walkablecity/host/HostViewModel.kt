package tw.com.walkablecity.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import kotlinx.coroutines.*
import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.userId

class HostViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {


    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val isPublic = MutableLiveData<Boolean>()
    val invited = MutableLiveData<List<Int>>()
    val type = MutableLiveData<EventType>()
    val target = MutableLiveData<EventTarget>()
    val startDate = MutableLiveData<Timestamp>()
    val endDate = MutableLiveData<Timestamp>()


    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _navigateToEvents = MutableLiveData<Boolean>(false)
    val navigateToEvents: LiveData<Boolean> get() = _navigateToEvents

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    fun navigateToEventsComplete(){
        _navigateToEvents.value = false
    }

    fun createEvent(){
        if(title.value == null || description.value == null || isPublic.value == null || invited.value == null
            || type.value == null || target.value == null || startDate.value == null || endDate.value == null){
            makeShortToast(R.string.not_complete_yet)
        }else{
            uploadEvent(
                Event(
                    id          = "${requireNotNull(type.value)}${now().seconds}${userId}",
                    title       = title.value,
                    description = description.value,
                    isPublic    = isPublic.value,
                    host        = userId,
                    invited     = requireNotNull(invited.value),
                    startDate   = startDate.value,
                    endDate     = endDate.value,
                    memberCount = 1,
                    member      = listOf(userId),
                    type        = type.value,
                    target      = target.value
                )
            )
        }
    }

    fun uploadEvent(event: Event){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            _navigateToEvents.value = when(val result = walkableRepository.createEvent(event)){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    false
                }
                is Result.Error->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    false
                }
                else->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    false
                }
            }
        }
    }
}

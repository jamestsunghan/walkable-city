package tw.com.walkablecity.host

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import kotlinx.coroutines.*
import tw.com.walkablecity.R
import tw.com.walkablecity.Util.dateToTimeStamp
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toDateLong
import tw.com.walkablecity.userId

class HostViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {


    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val isPublic = MutableLiveData<Boolean>()
    val invited = MutableLiveData<List<Int>>()

    val selectFQPosition = MutableLiveData<Int>()

    val frequencyType = Transformations.map(selectFQPosition){
        if(it != null && it > 0 ){
            FrequencyType.values()[it-1]
        }
        else null
    }

    val target = MutableLiveData<EventTarget>()

    val selectTypePosition = MutableLiveData<Int>()
    val type = Transformations.map(selectTypePosition){
        if(it>0){
            if(it ==1 || it == 2){
                EventType.FREQUENCY
            }else
            EventType.values()[it-2]
        }
        else null
    }
    val startDateDisplay = MutableLiveData<String>()
    val startDate = Transformations.map(startDateDisplay){

        when(it){
            null -> {
                Log.d("JJ", "date null")
                null
            }
            else -> {
                Log.d("JJ", "date $it")
                dateToTimeStamp(it)
            }
        }
    }

    val endDateDisplay = MutableLiveData<String>()
    val endDate = Transformations.map(endDateDisplay){

        when(it){
            null -> null
            else -> {
                val date = dateToTimeStamp(it)
                Timestamp(date?.seconds?.plus(ONE_DAY)?.minus(1) as Long, 0)
            }
        }
    }


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
            makeShortToast(R.string.event_not_complete)
        }else{
            uploadEvent(
                Event(
                    id          = "${requireNotNull(type.value).prefix}${now().toDateLong()}${userId}",
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

    private fun uploadEvent(event: Event){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            _navigateToEvents.value = when(val result = walkableRepository.createEvent(event)){
                is Result.Success ->{
                    _error.value  = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail->{
                    _error.value  = result.error
                    _status.value = LoadStatus.ERROR
                    false
                }
                is Result.Error->{
                    _error.value  = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    false
                }
                else->{
                    _error.value  = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    false
                }
            }
        }
    }

    companion object{
        const val ONE_DAY = 60 * 60 * 24
    }
}

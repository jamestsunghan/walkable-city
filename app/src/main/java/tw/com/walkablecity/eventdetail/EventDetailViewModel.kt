package tw.com.walkablecity.eventdetail

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository

class EventDetailViewModel(private val walkableRepository: WalkableRepository, val event: Event) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _walkResult = MutableLiveData<List<Float>>()
    val walkResult: LiveData<List<Float>> get() = _walkResult


    val eventMember = MutableLiveData<List<Friend>>().apply{
        value = event.member
    }


    var resultCount = 0

    private val _walkResultSingle = MutableLiveData<Float>()
    val walkResultSingle: LiveData<Float> get() = _walkResultSingle

    val typeColor = when(event.type){
        EventType.FREQUENCY -> WalkableApp.instance.resources.getColor(R.color.event_frequency, WalkableApp.instance.theme)
        EventType.DISTANCE_GROUP -> WalkableApp.instance.resources.getColor(R.color.event_distance_group, WalkableApp.instance.theme)
        EventType.DISTANCE_RACE -> WalkableApp.instance.resources.getColor(R.color.event_distance_race, WalkableApp.instance.theme)
        EventType.HOUR_GROUP -> WalkableApp.instance.resources.getColor(R.color.event_hour_group, WalkableApp.instance.theme)
        EventType.HOUR_RACE -> WalkableApp.instance.resources.getColor(R.color.event_hour_race, WalkableApp.instance.theme)
        null -> WalkableApp.instance.resources.getColor(R.color.primaryColor, WalkableApp.instance.theme)
    }

    val listMemberId = event.member.map{ requireNotNull(it.id)}

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{
        Log.d("JJ", "member list ${event.member}")
        getMemberWalkResult(requireNotNull(event.startDate), requireNotNull(event.target) ,listMemberId)
    }

    fun addToWalkResult(result: Float){
        _walkResult.value = if(walkResult.value.isNullOrEmpty()) listOf(result)
        else requireNotNull(walkResult.value).plus(result)

    }

    fun sortByAccomplish(){
        eventMember.value?.sortedBy { it.accomplish }?.reversed().apply{
            eventMember.value = this

        }
    }

    fun getMemberWalkResult(startTime: Timestamp, target: EventTarget, memberId: List<String>){

        if(resultCount == memberId.size){
            resultCount = 0
            return
        }

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = when(event.type){
                EventType.FREQUENCY      -> walkableRepository.getMemberWalkFrequencyResult(startTime, target, memberId[resultCount])
                EventType.DISTANCE_GROUP -> walkableRepository.getMemberWalkDistance(startTime, memberId[resultCount])
                EventType.DISTANCE_RACE  -> walkableRepository.getMemberWalkDistance(startTime, memberId[resultCount])
                EventType.HOUR_GROUP     -> walkableRepository.getMemberWalkHours(startTime, memberId[resultCount])
                EventType.HOUR_RACE      -> walkableRepository.getMemberWalkHours(startTime, memberId[resultCount])
                else ->null
            }

            _walkResultSingle.value = when(result){
                is Result.Success ->{
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
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }
            }

        }
    }

}

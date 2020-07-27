package tw.com.walkablecity.eventdetail

import android.graphics.Rect
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.lessThenTenPadStart
import tw.com.walkablecity.Util.setDp
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toNewInstance
import java.text.SimpleDateFormat
import java.util.*

class EventDetailViewModel(private val walkableRepository: WalkableRepository, val event: Event) : ViewModel() {

    private lateinit var timer: CountDownTimer

    val hostName = event.member.first { it.idCustom == event.host }.name

    val circleList = MutableLiveData<List<Float>>()

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _walkResult = MutableLiveData<List<Float>>()
    val walkResult: LiveData<List<Float>> get() = _walkResult

    private val _joinSuccess = MutableLiveData<Boolean>()
    val joinSuccess: LiveData<Boolean> get() = _joinSuccess

    private val _snapPosition = MutableLiveData<Int>()
    val snapPosition: LiveData<Int> get() = _snapPosition

    val currentCountDown = MutableLiveData<String>()

    var eventIsStarted = requireNotNull(event.startDate?.seconds) < now().seconds

    var countDownTime =
        if(eventIsStarted){
            (requireNotNull(event.endDate?.seconds) - now().seconds).times(ONE_SECOND)
        }else{
            (requireNotNull(event.startDate?.seconds) - now().seconds).times(ONE_SECOND)
        }




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

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left = setDp(16f).toInt()
            }
        }
    }

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val listOfList = MutableLiveData<List<FriendListWrapper>>()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        timer.cancel()

    }

    init{
        Log.d("JJ", "member list ${event.member}")
        getMemberWalkResult(requireNotNull(event.startDate), requireNotNull(event.target) ,listMemberId)

        getTimerStart(countDownTime)

//        checkEventStarted(eventIsStarted)

        val hostMember = event.member.find{ it.idCustom == event.host}


        for(item in requireNotNull(hostMember).accomplishFQ){
            val time = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN).format(item.date?.seconds?.times(1000))
            val frequencyMember = event.member.filter{friend->
                friend.accomplishFQ.find{mission->
                    val friendTime = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN).format(mission.date?.seconds?.times(1000))
                    friendTime == time
                } != null
            }

            val friendList = frequencyMember.map{
                it.toNewInstance()
            }

            val listToAdd: List<Friend> =  friendList.onEach {
                it.accomplish = it.accomplishFQ.find{mission->
                    val friendTime = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN).format(mission.date?.seconds?.times(1000))
                    friendTime == time}?.accomplish
            }

            Log.d("JJ_listToAdd", "list to add $listToAdd")
            val wrapper = FriendListWrapper(listToAdd.sortedByDescending { it.accomplish })
            listOfList.value = (listOfList.value ?: mutableListOf()).plus(wrapper) as MutableList<FriendListWrapper>


        }

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

    fun onGalleryScrollChange(layoutManager: RecyclerView.LayoutManager?, linearSnapHelper: LinearSnapHelper){
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let{
            layoutManager?.getPosition(snapView)?.let{
                if(it != snapPosition.value){
                    _snapPosition.value = it
                }
            }
        }
    }

    fun getTimerStart(time: Long){

        val lessThanADay = (time < ONE_DAY)
        Log.d("JJ","less than a day: $lessThanADay")
        val timeUnit = if(lessThanADay) ONE_SECOND else ONE_DAY
        timer = object : CountDownTimer(time, timeUnit){
            override fun onFinish() {
                currentCountDown.value = if(lessThanADay){
                    DONE.toString()
                }else{
                    eventIsStarted =
                        (requireNotNull(event.startDate?.seconds) < now().seconds)
                    countDownTime =
                        if(eventIsStarted){
                            (requireNotNull(event.endDate?.seconds) - now().seconds).times(ONE_SECOND)
                        }else{
                            (requireNotNull(event.startDate?.seconds) - now().seconds).times(ONE_SECOND)
                        }
                    getTimerStart(countDownTime)
                    DONE.toString()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / ONE_SECOND % 60
                val min = millisUntilFinished / ONE_SECOND / 60 % 60
                val hr = millisUntilFinished / ONE_SECOND / 60 / 60 % 24
                val day = millisUntilFinished / ONE_DAY
                currentCountDown.value =
                    when{
                        eventIsStarted && lessThanADay ->{
                            StringBuilder().append(getString(R.string.event_detail_timer))
                                .append(lessThenTenPadStart(hr)).append(":")
                                .append(lessThenTenPadStart(min)).append(":")
                                .append(lessThenTenPadStart(sec)).toString()

                        }
                        eventIsStarted && !lessThanADay ->{
                            StringBuilder().append(getString(R.string.event_detail_timer))
                                .append(String.format(getString(R.string.days_left), day)).toString()
                        }
                        !eventIsStarted && lessThanADay ->{
                            StringBuilder().append(getString(R.string.event_detail_pre_timer))
                                .append(lessThenTenPadStart(hr)).append(":")
                                .append(lessThenTenPadStart(min)).append(":")
                                .append(lessThenTenPadStart(sec)).toString()
                        }
                        !eventIsStarted && !lessThanADay ->{
                            StringBuilder().append(getString(R.string.event_detail_pre_timer))
                                .append(String.format(getString(R.string.days_left), day+1)).toString()
                        }
                        else -> ""
                    }

            }
        }
        timer.start()


    }

    fun joinEvent(){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result  = walkableRepository.joinEvent(requireNotNull(UserManager.user), event)

            _joinSuccess.value = when(result){
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

    fun joinPublicEvent(){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result  = walkableRepository.joinPublicEvent(requireNotNull(UserManager.user), event)

            _joinSuccess.value = when(result){
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

    companion object{
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val ONE_DAY = 24 * 60 * 60 * ONE_SECOND
    }

}

package tw.com.walkablecity.eventdetail

import android.graphics.Rect
import android.os.CountDownTimer
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
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.util.Util.lessThenTenPadStart
import tw.com.walkablecity.util.Util.setDp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import java.text.SimpleDateFormat
import java.util.*

class EventDetailViewModel(private val walkableRepository: WalkableRepository, val event: Event) :
    ViewModel() {

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
        if (eventIsStarted) {
            (requireNotNull(event.endDate?.seconds) - now().seconds).times(ONE_SECOND)
        } else {
            (requireNotNull(event.startDate?.seconds) - now().seconds).times(ONE_SECOND)
        }

    val eventMember = MutableLiveData<List<Friend>>().apply {
        value = event.member
    }

    val champ = Transformations.map(eventMember) {
        if (it.isNotEmpty()) it[0]
        else null
    }
    var resultCount = 0

    private val _walkResultSingle = MutableLiveData<Float>()
    val walkResultSingle: LiveData<Float> get() = _walkResultSingle

    val listMemberId = event.member.map { requireNotNull(it.id) }

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left = setDp(8f).toInt()
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

    init {
        Logger.d("member list ${event.member}")
        getMemberWalkResult(
            requireNotNull(event.startDate),
            requireNotNull(event.target),
            listMemberId
        )

        getTimerStart(countDownTime)

        val hostMember = event.member.find { it.idCustom == event.host }

        for (item in requireNotNull(hostMember).accomplishFQ) {
            val time =
                SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
                    .format(item.date?.seconds?.times(1000))

            val frequencyMember = event.member.filter { friend ->
                friend.accomplishFQ.find { mission ->
                    val friendTime = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
                        .format(mission.date?.seconds?.times(1000))

                    friendTime == time
                } != null
            }

            val friendList = frequencyMember.map {
                it.toNewInstance()
            }

            val listToAdd: List<Friend> = friendList.onEach {
                it.accomplish = it.accomplishFQ.find { mission ->
                    val friendTime = SimpleDateFormat(
                        "yyyyMMdd",
                        Locale.TAIWAN
                    ).format(mission.date?.seconds?.times(1000))
                    friendTime == time
                }?.accomplish
            }

            Logger.d("JJ_listToAdd list to add $listToAdd")

            val wrapper = FriendListWrapper(listToAdd.sortedByDescending { it.accomplish })

            listOfList.value = (listOfList.value
                ?: mutableListOf()).plus(wrapper) as MutableList<FriendListWrapper>

        }
    }

    fun addToWalkResult(result: Float) {
        _walkResult.value = if (walkResult.value.isNullOrEmpty()) {
            listOf(result)
        } else {
            requireNotNull(walkResult.value).plus(result)
        }
    }

    fun sortByAccomplish() {
        eventMember.value?.sortedBy { it.accomplish }?.reversed().apply {
            eventMember.value = this
        }
    }

    fun onGalleryScrollChange(
        layoutManager: RecyclerView.LayoutManager?,
        linearSnapHelper: LinearSnapHelper
    ) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                }
            }
        }
    }

    fun getTimerStart(time: Long) {

        val lessThanADay = (time < ONE_DAY)
        Logger.d("less than a day: $lessThanADay")
        val timeUnit = if (lessThanADay) ONE_SECOND else ONE_DAY
        timer = object : CountDownTimer(time, timeUnit) {
            override fun onFinish() {
                currentCountDown.value = if (lessThanADay) {
                    DONE.toString()
                } else {
                    eventIsStarted = (requireNotNull(event.startDate?.seconds) < now().seconds)
                    countDownTime = if (eventIsStarted) {
                        (requireNotNull(event.endDate?.seconds) - now().seconds).times(ONE_SECOND)
                    } else {
                        (requireNotNull(event.startDate?.seconds) - now().seconds).times(ONE_SECOND)
                    }

                    getTimerStart(countDownTime)

                    DONE.toString()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / ONE_SECOND % SECONDS
                val min = millisUntilFinished / ONE_SECOND / SECONDS % MINUTES
                val hr = millisUntilFinished / ONE_SECOND / SECONDS / MINUTES % HOURS
                val day = millisUntilFinished / ONE_DAY

                currentCountDown.value =
                    buildTimeString(
                        getTimePrefix(eventIsStarted), lessThanADay
                        , day, hr, min, sec
                    )

            }
        }
        timer.start()


    }

    private fun getTimePrefix(eventIsStarted: Boolean): String {
        return if (eventIsStarted) {
            getString(R.string.event_detail_timer)
        } else {
            getString(R.string.event_detail_pre_timer)
        }
    }

    private fun buildTimeString(
        prefix: String,
        lessThanADay: Boolean,
        day: Long,
        hr: Long,
        min: Long,
        sec: Long
    ): String {
        return if (lessThanADay) {
            StringBuilder().append(prefix)
                .append(lessThenTenPadStart(hr)).append(":")
                .append(lessThenTenPadStart(min)).append(":")
                .append(lessThenTenPadStart(sec)).toString()
        } else {
            StringBuilder().append(prefix)
                .append(String.format(getString(R.string.days_left), day))
                .toString()
        }
    }

    fun joinEvent() {

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.joinEvent(requireNotNull(UserManager.user), event)

            _joinSuccess.value = result.handleResultWith(_error, _status)

        }
    }

    fun joinPublicEvent() {

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            walkableRepository.joinPublicEvent(requireNotNull(UserManager.user), event).apply{
                _joinSuccess.value = handleResultWith(_error, _status)
            }
            
        }
    }


    fun getMemberWalkResult(startTime: Timestamp, target: EventTarget, memberId: List<String>) {

        if (resultCount == memberId.size) {
            resultCount = 0
            return
        }

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = if (event.type != EventType.FREQUENCY) {
                walkableRepository.getMemberWalks(startTime, memberId[resultCount])
                    .handleResultWith(_error, _status)
            } else null

            val friend = if (event.type == EventType.FREQUENCY) {
                walkableRepository.getUser(memberId[resultCount]).handleResultWith(_error, _status)
            } else null


            _walkResultSingle.value = when(event.type){
                EventType.FREQUENCY ->{
                    val accumulation =
                        if (target.distance == null) requireNotNull(friend?.accumulatedHour)  // frequency_hour
                        else requireNotNull(friend?.accumulatedKm)
                    when (target.frequencyType) {
                        FrequencyType.DAILY -> accumulation.daily
                        FrequencyType.WEEKLY -> accumulation.weekly
                        FrequencyType.MONTHLY -> accumulation.monthly
                        else -> 20200714f
                    }
                }
                EventType.HOUR_RACE      -> result?.sumBy{it.duration?.toInt() ?: 0}?.toFloat()
                EventType.HOUR_GROUP     -> result?.sumBy{it.duration?.toInt() ?: 0}?.toFloat()

                EventType.DISTANCE_RACE  -> result?.sumByDouble { it.distance?.toDouble() ?: 0.0 }?.toFloat()

                EventType.DISTANCE_GROUP -> result?.sumByDouble { it.distance?.toDouble() ?: 0.0 }?.toFloat()

                else -> null

            }

        }
    }

    fun keepGettingWalkResult(list: List<Float>) {
        resultCount += 1
        when {
            resultCount == listMemberId.size -> {

                eventMember.value?.mapIndexed { index, friend ->
                    requireNotNull(eventMember.value)[index].accomplish = list[index]
                    friend
                }
                sortByAccomplish()
                circleList.value = list.sortedByDescending { f -> f }.map { fa ->
                    fa.div(
                        event.target?.distance
                            ?: requireNotNull(event.target?.hour) * 60 * 60
                    )
                }

            }
            resultCount > listMemberId.size -> {
                resultCount = 0

            }
            else -> {
                getMemberWalkResult(
                    requireNotNull(event.startDate)
                    , requireNotNull(event.target), listMemberId
                )

            }
        }
    }

    companion object {
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val SECONDS = 60
        private const val MINUTES = 60
        private const val HOURS = 24
        private const val ONE_DAY = 24 * 60 * 60 * ONE_SECOND
    }

}

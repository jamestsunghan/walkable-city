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
import tw.com.walkablecity.ext.toDateLong
import tw.com.walkablecity.ext.toDateString
import tw.com.walkablecity.ext.toMissionFQ
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class EventDetailViewModel(private val walkableRepository: WalkableRepository, val event: Event) :
    ViewModel() {

    private lateinit var timer: CountDownTimer

    val hostName = event.member.first { member ->
        member.idCustom == event.host
    }.name

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

    val champ = Transformations.map(eventMember) { list ->
        if (list.isNotEmpty()) list[0]
        else null
    }
    var resultCount = 0

    private val _walkResultSingle = MutableLiveData<Float>()
    val walkResultSingle: LiveData<Float> get() = _walkResultSingle

    private val listMemberId = event.member.map { member ->
        requireNotNull(member.id)
    }

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

    val listOfList = MutableLiveData<MutableList<FriendListWrapper>>()

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

    }

    fun addToWalkResult(result: Float) {
        _walkResult.value = if (walkResult.value.isNullOrEmpty()) {
            listOf(result)
        } else {
            requireNotNull(walkResult.value).plus(result)
        }
    }

    private fun sortByAccomplish() {
        eventMember.value?.sortedByDescending { it.accomplish }?.apply {
            eventMember.value = this
        }
    }

    fun onGalleryScrollChange(
        layoutManager: RecyclerView.LayoutManager?,
        linearSnapHelper: LinearSnapHelper
    ) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let { position ->
                if (position != snapPosition.value) {
                    _snapPosition.value = position
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

                currentCountDown.value = buildTimeString(
                    getTimePrefix(eventIsStarted), lessThanADay, day, hr, min, sec
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

            walkableRepository.joinPublicEvent(requireNotNull(UserManager.user), event).apply {
                _joinSuccess.value = handleResultWith(_error, _status)
            }

        }
    }

    val listToAdd = event.member.map { friend ->
        friend.toNewInstance()
    }


    fun getMemberWalkResult(startTime: Timestamp, target: EventTarget, memberId: List<String>) {

        if (resultCount == memberId.size) {
            resultCount = 0
            return
        }

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result =
                walkableRepository.getMemberWalks(startTime, memberId[resultCount])
                    .handleResultWith(_error, _status)

            _walkResultSingle.value = when (event.type) {
                EventType.FREQUENCY -> {
                    when (target.frequencyType) {
                        FrequencyType.DAILY -> {
                            addMemberFQ(
                                memberId[resultCount], result,
                                ONE_DAY.div(ONE_SECOND), target.distance == null
                            )
                        }
                        FrequencyType.WEEKLY -> {
                            addMemberFQ(
                                memberId[resultCount], result,
                                ONE_DAY.div(ONE_SECOND).times(7), target.distance == null
                            )
                        }
                        FrequencyType.MONTHLY -> {
                            addMemberFQ(
                                memberId[resultCount], result,
                                baseOnHour =  target.distance == null
                            )
                        }
                    }
                    val resultFQ =
                        event.member.find { it.id == memberId[resultCount] }?.accomplishFQ?.last()?.accomplish
                            ?: 0f
                    if (target.distance == null) {
                        resultFQ.div(3600)
                    } else {
                        resultFQ
                    }
                }
                EventType.HOUR_RACE -> {
                    result?.sumBy { walk -> walk.duration?.toInt() ?: 0 }?.toFloat()
                }

                EventType.HOUR_GROUP -> {
                    result?.sumBy { walk -> walk.duration?.toInt() ?: 0 }?.toFloat()
                }

                EventType.DISTANCE_RACE -> {
                    result?.sumByDouble { walk -> walk.distance?.toDouble() ?: 0.0 }?.toFloat()
                }

                EventType.DISTANCE_GROUP -> {
                    result?.sumByDouble { walk -> walk.distance?.toDouble() ?: 0.0 }?.toFloat()
                }

                else -> null

            }
        }
    }

    fun addMemberFQ(memberId: String, result: List<Walk>?, timeUnit: Long = 0L, baseOnHour: Boolean) {
        var dateRangePoint = event.startDate?.seconds as Long
        val start = event.startDate.toDateLong().div(1000000)

        val startMonth = start.toInt().div(100) % 100
        val startYear = start.div(10000)

        val todayMonth = Calendar.getInstance().get(Calendar.MONTH)
        val todayYear = Calendar.getInstance().get(Calendar.YEAR)

        val startDateOfCalendar = Calendar.getInstance().apply{
            set(Calendar.YEAR, startYear.toInt())
            set(Calendar.MONTH, startMonth)
            set(Calendar.DAY_OF_MONTH, start.toInt() % 100)
        }

        val listSize = if (timeUnit ==0L){
            (todayYear - startYear).times(12) + todayMonth - startMonth
        } else {
            (now().seconds - (event.startDate as Timestamp).seconds)
                .div(timeUnit)
        } + 1

        val targetMember = listToAdd.find { it.id == memberId }
        for (time in 1..listSize) {

            val walkResult = result?.filter { walk ->
                (walk.endTime as Timestamp).seconds > dateRangePoint &&
                        (walk.endTime).seconds <= dateRangePoint + timeUnit
            }?.run {
                if (baseOnHour) {
                    sumByDouble {
                        it.duration?.toDouble() ?: 0.0
                    }
                } else {
                    sumByDouble {
                        it.distance?.toDouble() ?: 0.0
                    }
                }
            }?.toFloat() ?: 0f

            listToAdd.apply {
                targetMember?.accomplishFQ?.add(MissionFQ(Timestamp(dateRangePoint, 0), walkResult))
            }
            Logger.d("list to add ${listToAdd.size} $listToAdd")

            dateRangePoint = if (timeUnit == 0L) {
                startDateOfCalendar.add(Calendar.MONTH, 1)

                dateRangePoint + startDateOfCalendar.timeInMillis.div(ONE_SECOND)
            } else {
                dateRangePoint + timeUnit
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

                if (event.type == EventType.FREQUENCY) {

                    val listSize = listToAdd.first().accomplishFQ.size
                    for (item in 0 until listSize) {
                        val wrapper = FriendListWrapper(listToAdd.map {
                            it.toNewInstance()
                        }.apply {
                            this.onEach {
                                it.accomplish = it.accomplishFQ[item].accomplish
                            }
                        }.sortedByDescending {
                            it.accomplish
                        })
                        Logger.d("wrapper first ${wrapper.data.map { it.accomplish }}")
                        listOfList.value = (listOfList.value
                            ?: mutableListOf()).plus(wrapper) as MutableList<FriendListWrapper>
                    }
                }
            }

            resultCount > listMemberId.size -> resultCount = 0

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

package tw.com.walkablecity.host


import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import kotlinx.coroutines.*
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Util.dateToTimeStamp
import tw.com.walkablecity.util.Util.makeShortToast
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toDateLong

class HostViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {

    val title = MutableLiveData<String>()

    val description = MutableLiveData<String>()

    val isPublic = MutableLiveData<Boolean>(false)

    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>>
        get() = _friendList

    val searchId = MutableLiveData<String>()

    val friendListed = MediatorLiveData<List<Friend>?>().apply {
        addSource(searchId) { search ->
            value = when (search) {
                null -> friendList.value
                else -> {
                    when (val friendList = friendList.value) {
                        null -> friendList
                        else -> friendList.filter { friend ->
                            friend.name!!.startsWith(search)
                        }
                    }
                }
            }
        }

        addSource(friendList) { list ->
            value = when (list) {
                null -> null
                else -> {
                    when (val search = searchId.value) {
                        null -> list
                        else -> list.filter { friend ->
                            friend.name!!.startsWith(search)
                        }
                    }
                }
            }
        }
    }

    private val _navigateToHost = MutableLiveData<List<Friend>>()
    val navigateToHost: LiveData<List<Friend>>
        get() = _navigateToHost

    val selectFQPosition = MutableLiveData<Int>()

    val targetAmount = MutableLiveData<String>()

    val frequencyType = Transformations.map(selectFQPosition) {
        if (it != null && it > 0) {
            FrequencyType.values()[it - 1]
        } else null
    }

    val target = MediatorLiveData<EventTarget>().apply {

        addSource(frequencyType) {
            it?.let { type ->
                if (value == null) value = EventTarget()
                this.value?.frequencyType = type
                value = this.value

            }
        }
        addSource(targetAmount) {
            it?.let { amount ->
                if (value == null) value = EventTarget()
                when (selectTypePosition.value) {
                    in TYPE_POSITION_DISTANCE -> this.value?.distance =
                        if (amount == "") null else amount.toFloat()
                    in TYPE_POSITION_HOUR -> this.value?.hour =
                        if (amount == "") null else amount.toFloat()
                    else -> {
                    }
                }
                value = this.value

            }
        }
    }

    val selectTypePosition = MutableLiveData<Int>()
    val type = Transformations.map(selectTypePosition) { position ->
        if (position > 0) {
            if (position == 1 || position == 2) {
                EventType.FREQUENCY
            } else
                EventType.values()[position - 2]
        } else null
    }
    val startDateDisplay = MutableLiveData<String>()
    val startDate = Transformations.map(startDateDisplay) { display ->

        when (display) {
            null -> {
                Logger.d("date null")
                null
            }
            else -> {
                Logger.d("date $display")
                dateToTimeStamp(display)
            }
        }
    }

    val endDateDisplay = MutableLiveData<String>()

    val endDate = Transformations.map(endDateDisplay) { display ->

        when (display) {
            null -> null
            else -> {
                val date = dateToTimeStamp(display)
                Timestamp(date?.seconds?.plus(ONE_DAY)?.minus(1) as Long, 0)
            }
        }
    }

    private val eventStatus = Transformations.map(startDate) { time ->
        when (time) {
            null -> null
            else -> {
                if (time.seconds > now().seconds) {
                    EventStatus.UPCOMING
                } else {
                    EventStatus.ONGOING
                }
            }
        }
    }

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _navigateToEvents = MutableLiveData<Boolean>(false)
    val navigateToEvents: LiveData<Boolean>
        get() = _navigateToEvents

    private val _navigateToAddFriends = MutableLiveData<Boolean>(false)
    val navigateToAddFriends: LiveData<Boolean>
        get() = _navigateToAddFriends

    private val _addList = MutableLiveData<List<Friend>>()
    val addList: LiveData<List<Friend>>
        get() = _addList

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        UserManager.user?.id?.let { id ->
            getUserFriends(id)
        }
    }

    fun addSomeFriends() {
        _navigateToAddFriends.value = true
    }

    fun addSomeFriendsComplete() {
        _navigateToAddFriends.value = false
    }

    fun navigateToEventsComplete() {
        _navigateToEvents.value = false
    }

    fun createEvent() {

        if (title.value == null || description.value == null || isPublic.value == null || type.value == null
            || (target.value?.hour == null && target.value?.distance == null)
            || (frequencyType.value == null && type.value == EventType.FREQUENCY)
            || startDate.value == null || endDate.value == null || addList.value == null
        ) {

            makeShortToast(R.string.event_not_complete)
        } else {
            uploadEvent(
                Event(
                    id = requireNotNull(type.value).prefix +
                            "${now().toDateLong()}" +
                            "${UserManager.user?.idCustom}",
                    title = title.value,
                    description = description.value,
                    public = isPublic.value,
                    host = UserManager.user?.idCustom,
                    status = eventStatus.value,
                    invited = addList.value?.map { requireNotNull(it.id) } ?: listOf(),
                    startDate = startDate.value,
                    endDate = endDate.value,
                    memberCount = 1,
                    member = listOf(requireNotNull(UserManager.user).toFriend()),
                    type = type.value,
                    target = target.value
                )
            )
        }
    }

    private fun uploadEvent(event: Event) {

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.createEvent(event)

            _navigateToEvents.value = result.handleBooleanResultWith(_error, _status)

        }
    }

    fun addFriendToAddList(friend: Friend) {
        when (addList.value?.contains(friend)) {
            true -> {
            }
            else -> {
                _addList.value = (addList.value ?: listOf()).plus(friend)
            }
        }
    }

    fun removeFriendToAddList(friend: Friend) {
        when (addList.value?.contains(friend)) {
            true -> _addList.value = (addList.value ?: listOf(friend)).minus(friend)
            else -> {
            }
        }
    }

    fun friendSelected() {
        if (addList.value == null) {
            makeShortToast(R.string.no_friend_invited)
        } else {
            _navigateToHost.value = addList.value
        }
    }


    fun friendSelectedComplete() {
        _navigateToHost.value = null
    }

    private fun getUserFriends(userId: String) {

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getUserFriendSimple(userId)

            _friendList.value = result.handleResultWith(_error, _status)

        }
    }

    companion object {
        const val ONE_DAY = 60 * 60 * 24
        private val TYPE_POSITION_DISTANCE = listOf(1, 3, 4)
        private val TYPE_POSITION_HOUR = listOf(2, 5, 6)
    }
}

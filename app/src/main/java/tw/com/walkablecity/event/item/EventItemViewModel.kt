package tw.com.walkablecity.event.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.data.Event
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.event.EventPageType

class EventItemViewModel(val walkableRepository: WalkableRepository, val eventPage: EventPageType) :
    ViewModel() {

    private val _filter = MutableLiveData<EventType>()
    val filter: LiveData<EventType>
        get() = _filter

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _eventAllList = MutableLiveData<List<Event>>()
    val eventAllList: LiveData<List<Event>>
        get() = _eventAllList

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>>
        get() = _eventList

    private val _navigateToEventDetail = MutableLiveData<Event>()
    val navigateToEventDetail: LiveData<Event>
        get() = _navigateToEventDetail

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        getEventList(eventPage)
    }

    fun getEventListToFilter(list: List<Event>) {
        _eventList.value = list
    }

    fun seeAllEvent() {
        _eventList.value = eventAllList.value
        _filter.value = null
    }

    fun filterSwitch(sorting: EventType) {
        _eventList.value = eventAllList.value?.filter { event ->
            event.type == sorting
        }
        _filter.value = sorting
    }

    fun navigateToEventDetail(event: Event) {
        _navigateToEventDetail.value = event
    }

    fun navigateToDetailComplete() {
        _navigateToEventDetail.value = null
    }

    private fun getEventList(page: EventPageType) {

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            _eventAllList.value = when (page) {

                EventPageType.POPULAR -> {
                    walkableRepository.getPublicEvents()
                        .handleResultWith(_error, _status)?.sortedByDescending { event ->
                            event.memberCount
                        }
                }

                EventPageType.INVITED -> {
                    walkableRepository.getUserInvitation(requireNotNull(UserManager.user?.id))
                        .handleResultWith(_error, _status)
                }

                EventPageType.CHALLENGING -> {
                    walkableRepository.getNowAndFutureEvents()
                        .handleResultWith(_error, _status)?.filter { event ->
                            event.member.find { member ->
                                member.id == UserManager.user?.id
                            } != null
                        }
                }
            }
        }
    }
}
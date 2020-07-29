package tw.com.walkablecity.event.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.Event
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.event.EventPageType

class EventItemViewModel(val walkableRepository: WalkableRepository, val eventPage: EventPageType): ViewModel() {


    private val _filter = MutableLiveData<EventType>()
    val filter: LiveData<EventType> get() = _filter



    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _eventAllList = MutableLiveData<List<Event>>()
    val eventAllList: LiveData<List<Event>> get() = _eventAllList

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList

    private val _navigateToEventDetail = MutableLiveData<Event>()
    val navigateToEventDetail: LiveData<Event> get() = _navigateToEventDetail



    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{
        getEventList(eventPage)
    }

    fun getEventListToFilter(list: List<Event>){
        _eventList.value = list
    }

    fun seeAllEvent(){
        _eventList.value = eventAllList.value
        _filter.value = null
    }

    fun filterSwitch(sorting: EventType){
        _eventList.value = eventAllList.value?.filter{
            it.type == sorting
        }
        _filter.value = sorting
    }

    fun navigateToEventDetail(event: Event){
        _navigateToEventDetail.value = event
    }

    fun navigateToDetailComplete(){
        _navigateToEventDetail.value = null
    }

    private fun getEventList(page: EventPageType){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING
            val result = when(page){
                EventPageType.POPULAR -> walkableRepository.getPopularEvents()
                EventPageType.INVITED -> walkableRepository.getUserInvitation(requireNotNull(UserManager.user?.id))
                EventPageType.CHALLENGING -> walkableRepository.getUserChallenges(requireNotNull(UserManager.user))
            }

            _eventAllList.value = when(result){
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
                else->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }

            }

        }


    }


}
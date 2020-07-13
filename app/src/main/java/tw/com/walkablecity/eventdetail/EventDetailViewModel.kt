package tw.com.walkablecity.eventdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import tw.com.walkablecity.R
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.Event
import tw.com.walkablecity.data.EventType
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.source.WalkableRepository

class EventDetailViewModel(private val walkableRepository: WalkableRepository, val event: Event) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    val typeColor = when(event.type){
        EventType.FREQUENCY -> WalkableApp.instance.resources.getColor(R.color.event_frequency, WalkableApp.instance.theme)
        EventType.DISTANCE_GROUP -> WalkableApp.instance.resources.getColor(R.color.event_distance_group, WalkableApp.instance.theme)
        EventType.DISTANCE_RACE -> WalkableApp.instance.resources.getColor(R.color.event_distance_race, WalkableApp.instance.theme)
        EventType.HOUR_GROUP -> WalkableApp.instance.resources.getColor(R.color.event_hour_group, WalkableApp.instance.theme)
        EventType.HOUR_RACE -> WalkableApp.instance.resources.getColor(R.color.event_hour_race, WalkableApp.instance.theme)
        null-> WalkableApp.instance.resources.getColor(R.color.primaryColor, WalkableApp.instance.theme)
    }

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)



}

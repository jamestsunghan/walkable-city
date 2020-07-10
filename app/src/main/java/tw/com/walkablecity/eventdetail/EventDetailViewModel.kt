package tw.com.walkablecity.eventdetail

import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.Event
import tw.com.walkablecity.data.source.WalkableRepository

class EventDetailViewModel(private val walkableRepository: WalkableRepository, val event: Event) : ViewModel() {
    // TODO: Implement the ViewModel
}

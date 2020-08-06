package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.event.EventPageType
import tw.com.walkablecity.event.item.EventItemViewModel


@Suppress("UNCHECKED_CAST")
class EventPageViewModelFactory(
    private val walkableRepository: WalkableRepository,
    private val eventPage: EventPageType
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(EventItemViewModel::class.java) ->
                    EventItemViewModel(walkableRepository, eventPage)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}

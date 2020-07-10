package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.Event
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.eventdetail.EventDetailViewModel


@Suppress("UNCHECKED_CAST")
class EventVIewModelFactory(private val walkableRepository: WalkableRepository
                            , private val event: Event): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass){
            when{
                isAssignableFrom(EventDetailViewModel::class.java) ->
                    EventDetailViewModel(walkableRepository, event)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T

}
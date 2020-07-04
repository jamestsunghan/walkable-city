package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.withContext
import tw.com.walkablecity.data.source.DefaultWalkableRepository
import tw.com.walkablecity.loadroute.LoadRouteType
import tw.com.walkablecity.loadroute.route.RouteItemViewModel

@Suppress("UNCHECKED CAST")
class LoadRouteViewModelFactory(private val walkableRepository: DefaultWalkableRepository,
                                private val loadRouteType: LoadRouteType): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass){
            when{
                isAssignableFrom(RouteItemViewModel::class.java) ->
                    RouteItemViewModel(walkableRepository, loadRouteType)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
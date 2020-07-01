package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository

@Suppress("UNCHECKED_CAST")
class RouteViewModelFactory(private val walkableRepository: WalkableRepository, private val route: Route)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass){
            when{
//                isAssignableFrom(MainViewModel::class.java) ->
//                    MainViewModel(walkableRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
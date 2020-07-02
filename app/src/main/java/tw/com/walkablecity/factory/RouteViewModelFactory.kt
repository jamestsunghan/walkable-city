package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class RouteViewModelFactory(private val walkableRepository: WalkableRepository, val route: Route?)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass){
            when{
                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(walkableRepository, route)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
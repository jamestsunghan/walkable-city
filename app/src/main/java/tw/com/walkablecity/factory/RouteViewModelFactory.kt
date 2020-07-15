package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.detail.DetailViewModel
import tw.com.walkablecity.home.HomeViewModel
import tw.com.walkablecity.rating.RatingViewModel

@Suppress("UNCHECKED_CAST")
class RouteViewModelFactory(private val walkableRepository: WalkableRepository, val route: Route?)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass){
            when{

                isAssignableFrom(DetailViewModel::class.java) ->
                    DetailViewModel(walkableRepository, requireNotNull(route))

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
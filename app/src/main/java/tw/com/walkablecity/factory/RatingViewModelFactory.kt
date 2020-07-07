package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.HomeViewModel
import tw.com.walkablecity.rating.RatingType
import tw.com.walkablecity.rating.RatingViewModel
import tw.com.walkablecity.rating.item.RatingItemViewModel


@Suppress("UNCHECKED_CAST")
class RatingViewModelFactory(private val walkableRepository: WalkableRepository, private val route: Route?
                             , private val walk: Walk, private val type: RatingType?)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass){
            when{

                isAssignableFrom(RatingViewModel::class.java) ->
                    RatingViewModel(walkableRepository, route, walk, type)

                isAssignableFrom(RatingItemViewModel::class.java) ->
                    RatingItemViewModel(walkableRepository, route, walk, type)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
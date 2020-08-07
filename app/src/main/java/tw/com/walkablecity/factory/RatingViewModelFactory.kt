package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.HomeViewModel
import tw.com.walkablecity.home.createroute.CreateRouteDialogViewModel
import tw.com.walkablecity.rating.RatingType
import tw.com.walkablecity.rating.RatingViewModel
import tw.com.walkablecity.rating.item.RatingItemViewModel


@Suppress("UNCHECKED_CAST")
class RatingViewModelFactory(
    private val walkableRepository: WalkableRepository,
    private val route: Route?,
    private val walk: Walk,
    private val type: RatingType?,
    private val photoPoints: List<PhotoPoint>?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(RatingItemViewModel::class.java) ->
                    RatingItemViewModel(walkableRepository, route, walk, type, photoPoints)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
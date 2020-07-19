package tw.com.walkablecity.home.createroute

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.rating.RatingType

class CreateRouteDialogViewModel(val repo: WalkableRepository, val route: Route?, val walk: Walk, val type: RatingType?) : ViewModel() {


    private val _navigateToRating = MutableLiveData<Boolean>(false)
    val navigateToRating: LiveData<Boolean> get() = _navigateToRating

    private val _willCreate = MutableLiveData<Boolean>()
    val willCreate: LiveData<Boolean> get() = _willCreate


    fun willCreateRoute(){
        _willCreate.value = true
        _navigateToRating.value = true
    }

    fun willNotCreateRoute(){
        _willCreate.value = false
        _navigateToRating.value = true
    }

    fun navigationComplete(){
        _navigateToRating.value = false
    }

}

package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.HomeViewModel
import java.lang.IllegalArgumentException


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val walkableRepository: WalkableRepository,
                           private val route: Route?,
                           private val destination: LatLng?): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass){
            when{
                isAssignableFrom(HomeViewModel::class.java)->
                    HomeViewModel(walkableRepository, route, destination)
                else -> throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
            }
        } as T
}
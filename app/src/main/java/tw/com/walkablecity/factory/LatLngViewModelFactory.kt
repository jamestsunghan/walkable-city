package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.search.SearchViewModel


@Suppress("UNCHECKED_CAST")
class LatLngViewModelFactory(private val walkableRepository: WalkableRepository,
                             private val currentLocation: LatLng): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass){
            when{
                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(walkableRepository, currentLocation)
                else -> throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T


}
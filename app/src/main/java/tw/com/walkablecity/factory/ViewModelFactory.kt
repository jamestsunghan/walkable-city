package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.MainViewModel
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory( private val walkableRepository: WalkableRepository)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass){
            when{
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(walkableRepository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(walkableRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
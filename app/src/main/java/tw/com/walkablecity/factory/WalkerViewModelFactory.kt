package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.home.HomeViewModel


@Suppress("UNCHECKED_CAST")
class WalkerViewModelFactory(private val walkableRepository: WalkableRepository, private val walker: Walker?)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass){
            when{

//                isAssignableFrom(HomeViewModel::class.java) ->
//                    HomeViewModel(walkableRepository, walker)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
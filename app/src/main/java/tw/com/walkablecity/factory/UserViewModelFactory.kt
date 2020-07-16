package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.MainViewModel
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.profile.bestwalker.BestWalkersViewModel

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory( private val walkableRepository: WalkableRepository, private val user: User)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass){
            when{
//                isAssignableFrom(BestWalkersViewModel::class.java) ->
//                    BestWalkersViewModel(walkableRepository, user)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
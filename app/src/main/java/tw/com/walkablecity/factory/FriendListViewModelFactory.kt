package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.data.Friend
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.host.HostViewModel
import tw.com.walkablecity.host.add2event.AddFriend2EventViewModel

@Suppress("UNCHECKED_CAST")
class FriendListViewModelFactory(private val walkableRepository: WalkableRepository,
                                 private val friendList: List<Friend>?): ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass){

            when{
                isAssignableFrom(HostViewModel::class.java) ->
                    HostViewModel(walkableRepository, friendList)

                isAssignableFrom(AddFriend2EventViewModel::class.java) ->
                    AddFriend2EventViewModel(walkableRepository, friendList)

                else -> throw ClassCastException("Unknown ViewModel Class ${modelClass.name}")
            }

    } as T
}
package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.MainViewModel
import tw.com.walkablecity.addfriend.AddFriendViewModel
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.detail.DetailViewModel
import tw.com.walkablecity.event.EventViewModel
import tw.com.walkablecity.event.item.EventItemViewModel
import tw.com.walkablecity.favorite.FavoriteViewModel
import tw.com.walkablecity.home.HomeViewModel
import tw.com.walkablecity.host.HostViewModel
import tw.com.walkablecity.loadroute.LoadRouteViewModel
import tw.com.walkablecity.loadroute.route.RouteItemViewModel
import tw.com.walkablecity.login.LoginViewModel
import tw.com.walkablecity.profile.ProfileViewModel
import tw.com.walkablecity.ranking.RankingViewModel
import tw.com.walkablecity.rating.RatingViewModel
import tw.com.walkablecity.search.SearchViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory( private val walkableRepository: WalkableRepository)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass){
            when{
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(walkableRepository)

                isAssignableFrom(RankingViewModel::class.java) ->
                    RankingViewModel(walkableRepository)

                isAssignableFrom(FavoriteViewModel::class.java) ->
                    FavoriteViewModel(walkableRepository)

                isAssignableFrom(EventViewModel::class.java) ->
                    EventViewModel(walkableRepository)

                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(walkableRepository)

                isAssignableFrom(LoadRouteViewModel::class.java) ->
                    LoadRouteViewModel(walkableRepository)

                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(walkableRepository)

                isAssignableFrom(AddFriendViewModel::class.java) ->
                    AddFriendViewModel(walkableRepository)

                isAssignableFrom(HostViewModel::class.java) ->
                    HostViewModel(walkableRepository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(walkableRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
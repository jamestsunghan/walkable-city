package tw.com.walkablecity.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.com.walkablecity.MainViewModel
import tw.com.walkablecity.addfriend.AddFriendViewModel
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.event.EventViewModel
import tw.com.walkablecity.favorite.FavoriteViewModel
import tw.com.walkablecity.home.createroute.CreateRouteDialogViewModel
import tw.com.walkablecity.host.HostViewModel
import tw.com.walkablecity.login.LoginViewModel
import tw.com.walkablecity.profile.ProfileViewModel
import tw.com.walkablecity.profile.badge.BadgeViewModel
import tw.com.walkablecity.profile.bestwalker.BestWalkersViewModel
import tw.com.walkablecity.profile.explore.ExploreViewModel
import tw.com.walkablecity.profile.settings.SettingsViewModel
import tw.com.walkablecity.ranking.RankingViewModel
import tw.com.walkablecity.rating.RatingViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val walkableRepository: WalkableRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
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

                isAssignableFrom(AddFriendViewModel::class.java) ->
                    AddFriendViewModel(walkableRepository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(walkableRepository)

                isAssignableFrom(BestWalkersViewModel::class.java) ->
                    BestWalkersViewModel(walkableRepository)

                isAssignableFrom(SettingsViewModel::class.java) ->
                    SettingsViewModel(walkableRepository)

                isAssignableFrom(BadgeViewModel::class.java) ->
                    BadgeViewModel(walkableRepository)

                isAssignableFrom(ExploreViewModel::class.java) ->
                    ExploreViewModel(walkableRepository)

                isAssignableFrom(CreateRouteDialogViewModel::class.java) ->
                    CreateRouteDialogViewModel(walkableRepository)

                isAssignableFrom(RatingViewModel::class.java) ->
                    RatingViewModel(walkableRepository)

                isAssignableFrom(HostViewModel::class.java) ->
                    HostViewModel(walkableRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel Class ${modelClass.name}")
            }
        } as T
}
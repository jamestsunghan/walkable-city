package tw.com.walkablecity.ext

import androidx.fragment.app.Fragment
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.factory.*
import tw.com.walkablecity.loadroute.LoadRouteType
import tw.com.walkablecity.rating.RatingType

fun Fragment.getVMFactory(): ViewModelFactory{
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return ViewModelFactory(repo)
}

fun Fragment.getVMFactory(user: User): UserViewModelFactory{
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return UserViewModelFactory(repo, user)
}

fun Fragment.getVMFactory(route: Route?): RouteViewModelFactory {
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return RouteViewModelFactory(repo, route)
}

fun Fragment.getVMFactory(route: Route?, walk: Walk, type: RatingType?): RatingViewModelFactory {
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return RatingViewModelFactory(repo, route, walk, type)
}

fun Fragment.getVMFactory(loadRouteType: LoadRouteType): LoadRouteViewModelFactory {
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return LoadRouteViewModelFactory(repo, loadRouteType)
}
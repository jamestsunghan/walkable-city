package tw.com.walkablecity.ext

import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.event.EventPageType
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

fun Fragment.getVMFactory(route: Route?, walk: Walk, type: RatingType?, photoPoints: List<PhotoPoint>?): RatingViewModelFactory {
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return RatingViewModelFactory(repo, route, walk, type,photoPoints)
}

fun Fragment.getVMFactory(loadRouteType: LoadRouteType): LoadRouteViewModelFactory {
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return LoadRouteViewModelFactory(repo, loadRouteType)
}

fun Fragment.getVMFactory(eventPage: EventPageType): EventPageViewModelFactory {
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return EventPageViewModelFactory(repo, eventPage)
}

fun Fragment.getVMFactory(event: Event): EventVIewModelFactory{
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return EventVIewModelFactory(repo, event)
}

fun Fragment.getVMFactory(currentLocation: LatLng): LatLngViewModelFactory{
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return LatLngViewModelFactory(repo, currentLocation)
}

fun Fragment.getVMFactory(route: Route?,destination: LatLng?): HomeViewModelFactory{
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return HomeViewModelFactory(repo, route, destination)
}
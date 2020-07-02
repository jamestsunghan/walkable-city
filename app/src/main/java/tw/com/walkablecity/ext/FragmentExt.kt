package tw.com.walkablecity.ext

import androidx.fragment.app.Fragment
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.factory.RouteViewModelFactory
import tw.com.walkablecity.factory.UserViewModelFactory
import tw.com.walkablecity.factory.ViewModelFactory
import tw.com.walkablecity.factory.WalkerViewModelFactory

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

fun Fragment.getVMFactory(walker: Walker?): WalkerViewModelFactory {
    val repo = (requireContext().applicationContext as WalkableApp).repo
    return WalkerViewModelFactory(repo, walker)
}
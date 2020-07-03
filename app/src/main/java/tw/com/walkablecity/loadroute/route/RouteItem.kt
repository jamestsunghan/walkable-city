package tw.com.walkablecity.loadroute.route

import tw.com.walkablecity.data.Route

sealed class RouteItem{
    abstract val id: Long

    object Filter: RouteItem(){
        override val id: Long
            get() = Long.MIN_VALUE
    }

    data class LoadRoute(val route: Route): RouteItem(){
        override val id: Long
            get() = route.id
    }
}
package tw.com.walkablecity.loadroute.route

import tw.com.walkablecity.data.Route

sealed class RouteItem{
    abstract val id: String?

    object Filter: RouteItem(){
        override val id: String
            get() = Long.MIN_VALUE.toString()
    }

    data class LoadRoute(val route: Route): RouteItem(){
        override val id: String?
            get() = route.id
    }
}
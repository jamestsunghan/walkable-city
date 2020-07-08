package tw.com.walkablecity.data.source

import com.google.android.gms.maps.model.LatLng
import tw.com.walkablecity.data.*

class DefaultWalkableRepository(private val remote: WalkableDataSource): WalkableRepository {
    override suspend fun getAllRoute(): Result<List<Route>> {
        return remote.getAllRoute()
    }

    override suspend fun getUserFavoriteRoutes(userId: Int): Result<List<Route>> {
        return remote.getUserFavoriteRoutes(userId)
    }

    override suspend fun getUserRoutes(userId: Int): Result<List<Route>> {
        return remote.getUserRoutes(userId)
    }

    override suspend fun getRoutesNearby(userLocation: LatLng): Result<List<Route>> {
        return remote.getRoutesNearby(userLocation)
    }

    override suspend fun drawPath(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng>
    ): Result<DirectionResult> {
        return remote.drawPath(origin, destination, waypoints)
    }

    override suspend fun getUserCurrentLocation(): Result<LatLng> {
        return remote.getUserCurrentLocation()
    }

    override suspend fun updateRouteRating(rating: RouteRating, route: Route, userId: Int): Result<Boolean> {
        return remote.updateRouteRating(rating, route, userId)
    }

    override suspend fun createRouteByUser(route: Route): Result<Boolean> {
        return remote.createRouteByUser(route)
    }
}
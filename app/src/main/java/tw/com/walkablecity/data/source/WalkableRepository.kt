package tw.com.walkablecity.data.source

import com.google.android.gms.maps.model.LatLng
import tw.com.walkablecity.data.DirectionResult
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route

interface WalkableRepository {
    suspend fun getAllRoute(): Result<List<Route>>
    suspend fun getUserFavoriteRoutes(userId: Int): Result<List<Route>>
    suspend fun getUserRoutes(userId: Int): Result<List<Route>>
    suspend fun getRoutesNearby(userLocation: LatLng): Result<List<Route>>
    suspend fun drawPath(origin: LatLng, destination: LatLng, waypoints: List<LatLng>): Result<DirectionResult>
    suspend fun getUserCurrentLocation(): Result<LatLng>
}
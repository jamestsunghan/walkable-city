package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.data.*

interface WalkableRepository {
    suspend fun getAllRoute(): Result<List<Route>>

    suspend fun getRoutesRanking(sorting: RouteSorting, timeMin: Int, timeMax: Int): Result<List<Route>>
    suspend fun getUserFavoriteRoutes(userId: Int): Result<List<Route>>
    suspend fun getUserRoutes(userId: Int): Result<List<Route>>
    suspend fun getRoutesNearby(userLocation: LatLng): Result<List<Route>>
    suspend fun drawPath(origin: LatLng, destination: LatLng, waypoints: List<LatLng>): Result<DirectionResult>
    suspend fun getUserCurrentLocation(): Result<LatLng>
    suspend fun updateRouteRating(rating: RouteRating, route: Route, userId: Int): Result<Boolean>
    suspend fun createRouteByUser(route: Route): Result<Boolean>
    suspend fun getRouteMapImage(center: GeoPoint, zoom: Int, path: List<GeoPoint>): Result<MapImageResult>
    suspend fun getRouteMapImageUrl(routeId: Long, bitmap: Bitmap): Result<String>
    suspend fun getRouteComments(routeId: Long): Result<List<Comment>>
}
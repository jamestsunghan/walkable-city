package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.data.*

interface WalkableRepository {
    suspend fun getAllRoute(): Result<List<Route>>

    suspend fun getRoutesRanking(sorting: RouteSorting, timeMin: Int, timeMax: Int): Result<List<Route>>
    suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>>
    suspend fun getUserRoutes(userId: String): Result<List<Route>>
    suspend fun getRoutesNearby(userLocation: LatLng): Result<List<Route>>
    suspend fun drawPath(origin: LatLng, destination: LatLng, waypoints: List<LatLng>): Result<DirectionResult>
    suspend fun getUserCurrentLocation(): Result<LatLng>
    suspend fun updateRouteRating(rating: RouteRating, route: Route, userId: String): Result<Boolean>
    suspend fun createRouteByUser(route: Route): Result<Boolean>
    suspend fun getRouteMapImage(center: GeoPoint, zoom: Int, path: List<GeoPoint>): Result<MapImageResult>
    suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String>
    suspend fun getRouteComments(routeId: String): Result<List<Comment>>

    suspend fun addUserToFollowers(userId: String, route: Route): Result<Boolean>
    suspend fun removeUserFromFollowers(userId: String, route: Route): Result<Boolean>

    suspend fun getUserInvitation(userId: String): Result<List<Event>>
    suspend fun getUserChallenges(userId: String): Result<List<Event>>
    suspend fun getPopularEvents(): Result<List<Event>>

    suspend fun createEvent(event: Event): Result<Boolean>
}
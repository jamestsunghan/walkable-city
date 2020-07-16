package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.data.*

interface WalkableDataSource {
    suspend fun getAllRoute(): Result<List<Route>>
    suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>>
    suspend fun getRoutesRanking(sorting: RouteSorting, timeMin: Int, timeMax: Int): Result<List<Route>>
    suspend fun getUserRoutes(userId: String): Result<List<Route>>
    suspend fun getRoutesNearby(userLocation: LatLng): Result<List<Route>>
    suspend fun drawPath(origin: LatLng, destination: LatLng, waypoints: List<LatLng>): Result<DirectionResult>
    suspend fun getUserCurrentLocation(): Result<LatLng>

    suspend fun updateWalks(walk: Walk, user: User): Result<Boolean>
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

    suspend fun firebaseAuthWithGoogle(idToken: String?): Result<FirebaseUser>

    suspend fun signUpUser(user: User): Result<User>
    suspend fun getUser(userId: String): Result<User?>
    suspend fun checkIdCustomBeenUsed(idCustom: String): Result<Boolean>

    suspend fun searchFriendWithId(idCustom: String): Result<Friend?>
    suspend fun addFriend(friend: Friend, user: User): Result<Boolean>
    suspend fun checkFriendAdded(idCustom: String, userId: String): Result<Boolean>

    suspend fun getUserFriends(userId: String): Result<List<User>>


    suspend fun getMemberWalkDistance(eventStartTime: Timestamp, memberId: String): Result<Float>
    suspend fun getMemberWalkHours(eventStartTime: Timestamp, memberId: String): Result<Float>
    suspend fun getMemberWalkFrequencyResult(eventStartTime: Timestamp, target: EventTarget, memberId: String): Result<Float>


}
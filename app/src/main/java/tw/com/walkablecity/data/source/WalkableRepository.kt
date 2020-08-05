package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.data.*

interface WalkableRepository {


    suspend fun getAllRoute(): Result<List<Route>>
    suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>>
    suspend fun getUserRoutes(userId: String): Result<List<Route>>
    suspend fun drawPath(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng>
    ): Result<DirectionResult>

    suspend fun getUserCurrentLocation(): Result<LatLng>

    suspend fun updateWalks(walk: Walk, user: User): Result<Boolean>
    suspend fun updateRouteRating(
        rating: RouteRating,
        route: Route,
        userId: String,
        comment: Comment?
    ): Result<Boolean>

    suspend fun createRouteByUser(route: Route): Result<Boolean>
    suspend fun uploadPhotoPoints(routeId: String, photoPoints: List<PhotoPoint>): Result<Boolean>
    suspend fun downloadPhotoPoints(routeId: String): Result<List<PhotoPoint>>

    suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String>
    suspend fun getRouteComments(routeId: String): Result<List<Comment>>

    suspend fun addUserToFollowers(userId: String, route: Route): Result<Boolean>
    suspend fun removeUserFromFollowers(userId: String, route: Route): Result<Boolean>

    suspend fun getPublicEvents(): Result<List<Event>>
    suspend fun getUserEvents(userId: String): Result<List<Event>>
    suspend fun getUserInvitation(userId: String): Result<List<Event>>
    suspend fun getNowAndFutureEvents(): Result<List<Event>>

    suspend fun createEvent(event: Event): Result<Boolean>
    suspend fun joinEvent(user: User, event: Event): Result<Boolean>
    suspend fun joinPublicEvent(user: User, event: Event): Result<Boolean>

    suspend fun firebaseAuthWithGoogle(idToken: String?): Result<FirebaseUser>

    suspend fun signUpUser(user: User): Result<User>
    suspend fun getUser(userId: String): Result<User?>
    suspend fun checkIdCustomBeenUsed(idCustom: String): Result<Boolean>

    suspend fun searchFriendWithId(idCustom: String): Result<User?>
    suspend fun addFriend(friend: Friend, user: User): Result<Boolean>
    suspend fun checkFriendAdded(idCustom: String, userId: String): Result<Boolean>

    suspend fun getUserFriendSimple(userId: String): Result<List<Friend>>
    suspend fun getUserFriends(userId: String): Result<List<User>>
    suspend fun getUserWalks(userId: String): Result<List<Walk>>
    suspend fun getUserLatestWalk(userId: String): Result<Walk?>

    suspend fun getMemberWalks(eventStartTime: Timestamp, memberId: String): Result<List<Walk>>

    suspend fun updateEvents(
        user: User?,
        eventList: List<Event>,
        type: FrequencyType
    ): Result<Boolean>

    suspend fun getWeather(currentLocation: LatLng): Result<WeatherResult>

    suspend fun updateWeatherNotification(activate: Boolean, userId: String): Result<Boolean>
    suspend fun updateMealNotification(activate: Boolean, userId: String): Result<Boolean>


}
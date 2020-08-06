package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.data.*

interface WalkableDataSource {

    /**
     * query for collection Route in firestore.
     */

    suspend fun getAllRoute(): Result<List<Route>> // route related
    suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>> // route related
    suspend fun getUserRoutes(userId: String): Result<List<Route>> // route related

    /**
     * update query for sub-collection, field in Route in firestore.
     * Return boolean for result.
     */

    suspend fun updateRouteRating(
        rating: RouteRating,
        route: Route,
        userId: String,
        comment: Comment?
    ): Result<Boolean>

    suspend fun uploadPhotoPoints(routeId: String, photoPoints: List<PhotoPoint>): Result<Boolean>
    suspend fun createRouteByUser(route: Route): Result<Boolean>
    suspend fun addUserToFollowers(userId: String, route: Route): Result<Boolean>
    suspend fun removeUserFromFollowers(userId: String, route: Route): Result<Boolean>

    /**
     * query for sub-collection, field in Route in firestore.
     */

    suspend fun downloadPhotoPoints(routeId: String): Result<List<PhotoPoint>>
    suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String>
    suspend fun getRouteComments(routeId: String): Result<List<Comment>>


    /**
     * query for collection Event in firestore.
     * Return list of Events
     */

    suspend fun getAllEvents(): Result<List<Event>>
    suspend fun getPublicEvents(): Result<List<Event>>
    suspend fun getNowAndFutureEvents(): Result<List<Event>>
    suspend fun getUserInvitation(userId: String): Result<List<Event>>

    /**
     * update query for sub-collection, field in Event in firestore.
     * Return boolean for result.
     */

    suspend fun createEvent(event: Event): Result<Boolean>
    suspend fun joinEvent(user: User, event: Event): Result<Boolean>
    suspend fun joinPublicEvent(user: User, event: Event): Result<Boolean>
    suspend fun updateEvents(
        user: User?,
        eventList: List<Event>,
        type: FrequencyType
    ): Result<Boolean>

    /**
     * query for user authentication.
     */

    suspend fun firebaseAuthWithGoogle(idToken: String?): Result<FirebaseUser>

    /**
     * query for collection User in firestore.
     */


    suspend fun getUser(userId: String): Result<User?>
    suspend fun searchFriendWithId(idCustom: String): Result<User?>
    suspend fun getFriendsById(ids: List<String>): Result<List<User>>

    /**
     * query for update or to check sub-collection, field in User in firestore.
     * Return boolean for result.
     */

    suspend fun signUpUser(user: User): Result<Boolean>
    suspend fun checkIdCustomBeenUsed(idCustom: String): Result<Boolean>
    suspend fun updateWalks(walk: Walk, user: User): Result<Boolean> // user - walk

    suspend fun addFriend(friend: Friend, user: User): Result<Boolean>
    suspend fun checkFriendAdded(idCustom: String, userId: String): Result<Boolean>

    suspend fun updateWeatherNotification(activate: Boolean, userId: String): Result<Boolean>
    suspend fun updateMealNotification(activate: Boolean, userId: String): Result<Boolean>

    /**
     * query for sub-collection, field in User in firestore.
     */

    suspend fun getUserFriendSimple(userId: String): Result<List<Friend>>
    suspend fun getUserWalks(userId: String): Result<List<Walk>>
    suspend fun getUserLatestWalk(userId: String): Result<Walk?>
    suspend fun getMemberWalks(eventStartTime: Timestamp, memberId: String): Result<List<Walk>>

    /**
     * query for location or direction on map.
     */

    suspend fun drawPath(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng>
    ): Result<DirectionResult> //google map api

    suspend fun getUserCurrentLocation(): Result<Location?> // location api

    /**
     * query for weather.
     */

    suspend fun getWeather(currentLocation: LatLng): Result<WeatherResult>



}
package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.directionresult.DirectionResult
import tw.com.walkablecity.data.weatherresult.WeatherResult

interface WalkableRepository {

    /**
     * get collection Route from data source.
     */

    suspend fun getAllRoute(): Result<List<Route>> // route related
    suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>> // route related
    suspend fun getUserRoutes(userId: String): Result<List<Route>> // route related

    /**
     * update sub-collection, field in Route to data source.
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
     * get sub-collection, field in Route from data source.
     */

    suspend fun downloadPhotoPoints(routeId: String): Result<List<PhotoPoint>>
    suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String>
    suspend fun getRouteComments(routeId: String): Result<List<Comment>>


    /**
     * get Event list from data source.
     * Return list of Events
     */

    suspend fun getAllEvents(): Result<List<Event>>
    suspend fun getPublicEvents(): Result<List<Event>>
    suspend fun getNowAndFutureEvents(): Result<List<Event>>
    suspend fun getUserInvitation(userId: String): Result<List<Event>>


    /**
     * update sub-collection, field in Event to data source.
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
     * Get User from data source.
     */

    suspend fun getUser(userId: String): Result<User?>
    suspend fun searchFriendWithId(idCustom: String): Result<User?>
    suspend fun getFriendsById(ids: List<String>): Result<List<User>>
    /**
     * Update or to check sub-collection, field in User in data source.
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
     * Get sub-collection, field in User from data source.
     */

    suspend fun getUserFriendSimple(userId: String): Result<List<Friend>>
    suspend fun getUserWalks(userId: String): Result<List<Walk>>
    suspend fun getUserLatestWalk(userId: String): Result<Walk?>
    suspend fun getMemberWalks(eventStartTime: Timestamp, memberId: String): Result<List<Walk>>

    /**
     * Get location or direction on map.
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
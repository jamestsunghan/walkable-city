package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import tw.com.walkablecity.data.*

class DefaultWalkableRepository(private val remote: WalkableDataSource) : WalkableRepository {
    override suspend fun getAllRoute(): Result<List<Route>> {
        return remote.getAllRoute()
    }

    override suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>> {
        return remote.getUserFavoriteRoutes(userId)
    }

    override suspend fun getUserRoutes(userId: String): Result<List<Route>> {
        return remote.getUserRoutes(userId)
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

    override suspend fun updateWalks(walk: Walk, user: User): Result<Boolean> {
        return remote.updateWalks(walk, user)
    }

    override suspend fun updateRouteRating(
        rating: RouteRating,
        route: Route,
        userId: String,
        comment: Comment?
    ): Result<Boolean> {
        return remote.updateRouteRating(rating, route, userId, comment)
    }

    override suspend fun createRouteByUser(route: Route): Result<Boolean> {
        return remote.createRouteByUser(route)
    }

    override suspend fun uploadPhotoPoints(
        routeId: String,
        photoPoints: List<PhotoPoint>
    ): Result<Boolean> {
        return remote.uploadPhotoPoints(routeId, photoPoints)
    }

    override suspend fun downloadPhotoPoints(routeId: String): Result<List<PhotoPoint>> {
        return remote.downloadPhotoPoints(routeId)
    }

    override suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String> {
        return remote.getRouteMapImageUrl(routeId, bitmap)
    }

    override suspend fun getRouteComments(routeId: String): Result<List<Comment>> {
        return remote.getRouteComments(routeId)
    }

    override suspend fun addUserToFollowers(userId: String, route: Route): Result<Boolean> {
        return remote.addUserToFollowers(userId, route)
    }

    override suspend fun removeUserFromFollowers(userId: String, route: Route): Result<Boolean> {
        return remote.removeUserFromFollowers(userId, route)
    }

    override suspend fun getPublicEvents(): Result<List<Event>> {
        return remote.getPublicEvents()
    }

    override suspend fun getUserEvents(userId: String): Result<List<Event>> {
        return remote.getUserEvents(userId)
    }

    override suspend fun getNowAndFutureEvents(): Result<List<Event>> {
        return remote.getNowAndFutureEvents()
    }

    override suspend fun getUserInvitation(userId: String): Result<List<Event>> {
        return remote.getUserInvitation(userId)
    }

    override suspend fun createEvent(event: Event): Result<Boolean> {
        return remote.createEvent(event)
    }

    override suspend fun joinEvent(user: User, event: Event): Result<Boolean> {
        return remote.joinEvent(user, event)
    }

    override suspend fun joinPublicEvent(user: User, event: Event): Result<Boolean> {
        return remote.joinPublicEvent(user, event)
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String?): Result<FirebaseUser> {
        return remote.firebaseAuthWithGoogle(idToken)
    }

    override suspend fun signUpUser(user: User): Result<User> {
        return remote.signUpUser(user)
    }

    override suspend fun checkIdCustomBeenUsed(idCustom: String): Result<Boolean> {
        return remote.checkIdCustomBeenUsed(idCustom)


    }

    override suspend fun searchFriendWithId(idCustom: String): Result<User?> {
        return remote.searchFriendWithId(idCustom)
    }

    override suspend fun addFriend(friend: Friend, user: User): Result<Boolean> {
        return remote.addFriend(friend, user)
    }

    override suspend fun checkFriendAdded(idCustom: String, userId: String): Result<Boolean> {
        return remote.checkFriendAdded(idCustom, userId)
    }

    override suspend fun getUser(userId: String): Result<User?> {
        return remote.getUser(userId)
    }

    override suspend fun getMemberWalks(
        eventStartTime: Timestamp,
        memberId: String
    ): Result<List<Walk>> {
        return remote.getMemberWalks(eventStartTime, memberId)
    }

    override suspend fun getUserFriends(userId: String): Result<List<User>> {
        return remote.getUserFriends(userId)
    }

    override suspend fun getUserFriendSimple(userId: String): Result<List<Friend>> {
        return remote.getUserFriendSimple(userId)
    }

    override suspend fun getUserWalks(userId: String): Result<List<Walk>> {
        return remote.getUserWalks(userId)
    }

    override suspend fun getUserLatestWalk(userId: String): Result<Walk?> {
        return remote.getUserLatestWalk(userId)
    }

    override suspend fun updateEvents(
        user: User?,
        eventList: List<Event>,
        type: FrequencyType
    ): Result<Boolean> {
        return remote.updateEvents(user, eventList, type)
    }

    override suspend fun getWeather(currentLocation: LatLng): Result<WeatherResult> {
        return remote.getWeather(currentLocation)
    }

    override suspend fun updateWeatherNotification(
        activate: Boolean,
        userId: String
    ): Result<Boolean> {
        return remote.updateWeatherNotification(activate, userId)
    }

    override suspend fun updateMealNotification(
        activate: Boolean,
        userId: String
    ): Result<Boolean> {
        return remote.updateMealNotification(activate, userId)
    }
}
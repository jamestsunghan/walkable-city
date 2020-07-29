package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.data.*

class DefaultWalkableRepository(private val remote: WalkableDataSource): WalkableRepository {
    override suspend fun getAllRoute(): Result<List<Route>> {
        return remote.getAllRoute()
    }

    override suspend fun getRoutesRanking(
        sorting: RouteSorting,
        timeMin: Int,
        timeMax: Int
    ): Result<List<Route>> {
        return remote.getRoutesRanking(sorting, timeMin, timeMax)
    }

    override suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>> {
        return remote.getUserFavoriteRoutes(userId)
    }

    override suspend fun getUserRoutes(userId: String): Result<List<Route>> {
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

    override suspend fun updateWalks(walk: Walk, user: User): Result<Boolean> {
        return remote.updateWalks(walk, user)
    }

    override suspend fun updateRouteRating(rating: RouteRating, route: Route, userId: String): Result<Boolean> {
        return remote.updateRouteRating(rating, route, userId)
    }

    override suspend fun createRouteByUser(route: Route): Result<Boolean> {
        return remote.createRouteByUser(route)
    }

    override suspend fun uploadPhotoPoints(routeId: String, photoPoints: List<PhotoPoint>): Result<Boolean> {
        return remote.uploadPhotoPoints(routeId, photoPoints)
    }

    override suspend fun downloadPhotoPoints(routeId: String): Result<List<PhotoPoint>> {
        return remote.downloadPhotoPoints(routeId)
    }

    override suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String> {
        return remote.getRouteMapImageUrl(routeId, bitmap)
    }

    override suspend fun getRouteMapImage(
        center: GeoPoint,
        zoom: Int,
        path: List<GeoPoint>
    ): Result<MapImageResult> {
        return remote.getRouteMapImage(center,zoom, path)
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

    override suspend fun getPopularEvents(): Result<List<Event>> {
        return remote.getPopularEvents()
    }

    override suspend fun getUserChallenges(user: User): Result<List<Event>> {
        return remote.getUserChallenges(user)
    }

    override suspend fun getUserParticipateEvent(user: User): Result<List<Event>> {
        return remote.getUserParticipateEvent(user)
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

    override suspend fun searchFriendWithId(idCustom: String): Result<Friend?> {
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


    override suspend fun getMemberWalkDistance(eventStartTime: Timestamp, memberId: String): Result<Float> {
        return remote.getMemberWalkDistance(eventStartTime, memberId)
    }

    override suspend fun getMemberWalkHours(eventStartTime: Timestamp, memberId: String): Result<Float> {
        return remote.getMemberWalkHours(eventStartTime, memberId)
    }

    override suspend fun getMemberWalkFrequencyResult(
        eventStartTime: Timestamp,
        target: EventTarget,
        memberId: String
    ): Result<Float> {
        return remote.getMemberWalkFrequencyResult(eventStartTime, target, memberId)
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

    override suspend fun updateDailyEvents(user: User?, eventList: List<Event>): Result<Boolean> {
        return remote.updateDailyEvents(user, eventList)
    }

    override suspend fun updateWeeklyEvents(user: User?, eventList: List<Event>): Result<Boolean> {
        return remote.updateWeeklyEvents(user, eventList)
    }

    override suspend fun updateMonthlyEvents(user: User?, eventList: List<Event>): Result<Boolean> {
        return remote.updateMonthlyEvents(user, eventList)
    }

    override suspend fun getWeather(currentLocation: LatLng): Result<WeatherResult> {
        return remote.getWeather(currentLocation)
    }

    override suspend fun updateWeatherNotification(activate: Boolean, userId: String): Result<Boolean> {
        return remote.updateWeatherNotification(activate, userId)
    }

    override suspend fun updateMealNotification(activate: Boolean, userId: String): Result<Boolean> {
        return remote.updateMealNotification(activate, userId)
    }
}
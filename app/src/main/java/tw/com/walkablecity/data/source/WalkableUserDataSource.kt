package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.data.*

object WalkableUserDataSource: WalkableDataSource {
    override suspend fun getAllRoute(): Result<List<Route>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getRoutesRanking(
        sorting: RouteSorting,
        timeMin: Int,
        timeMax: Int
    ): Result<List<Route>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserRoutes(userId: String): Result<List<Route>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getRoutesNearby(userLocation: LatLng): Result<List<Route>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun drawPath(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng>
    ): Result<DirectionResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserCurrentLocation(): Result<LatLng> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateWalks(walk: Walk, user: User): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateRouteRating(
        rating: RouteRating,
        route: Route,
        userId: String,
        comment: Comment?
    ): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun createRouteByUser(route: Route): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun uploadPhotoPoints(
        routeId: String,
        photoPoints: List<PhotoPoint>
    ): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun downloadPhotoPoints(routeId: String): Result<List<PhotoPoint>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getRouteMapImage(
        center: GeoPoint,
        zoom: Int,
        path: List<GeoPoint>
    ): Result<MapImageResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getRouteComments(routeId: String): Result<List<Comment>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun addUserToFollowers(userId: String, route: Route): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun removeUserFromFollowers(userId: String, route: Route): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getPopularEvents(): Result<List<Event>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserEvents(userId: String): Result<List<Event>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserInvitation(userId: String): Result<List<Event>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserChallenges(user: User): Result<List<Event>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun createEvent(event: Event): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun joinEvent(user: User, event: Event): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun joinPublicEvent(user: User, event: Event): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String?): Result<FirebaseUser> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun signUpUser(user: User): Result<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUser(userId: String): Result<User?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun checkIdCustomBeenUsed(idCustom: String): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun searchFriendWithId(idCustom: String): Result<Friend?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun addFriend(friend: Friend, user: User): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun checkFriendAdded(idCustom: String, userId: String): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserFriendSimple(userId: String): Result<List<Friend>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserFriends(userId: String): Result<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserWalks(userId: String): Result<List<Walk>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUserLatestWalk(userId: String): Result<Walk?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getMemberWalkDistance(
        eventStartTime: Timestamp,
        memberId: String
    ): Result<Float> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getMemberWalkHours(
        eventStartTime: Timestamp,
        memberId: String
    ): Result<Float> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getMemberWalkFrequencyResult(
        eventStartTime: Timestamp,
        target: EventTarget,
        memberId: String
    ): Result<Float> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateEvents(
        user: User?,
        eventList: List<Event>,
        type: FrequencyType
    ): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getWeather(currentLocation: LatLng): Result<WeatherResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateWeatherNotification(
        activate: Boolean,
        userId: String
    ): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateMealNotification(
        activate: Boolean,
        userId: String
    ): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
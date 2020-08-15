package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.util.Util.isInternetConnected
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.directionresult.DirectionResult
import tw.com.walkablecity.data.weatherresult.WeatherResult
import tw.com.walkablecity.ext.*
import tw.com.walkablecity.network.WalkableApi
import tw.com.walkablecity.network.WeatherApi
import tw.com.walkablecity.util.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object WalkableRemoteDataSource : WalkableDataSource {

    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private val fusedLocationClient = FusedLocationProviderClient(WalkableApp.instance)
    private val auth = Firebase.auth

    /**
     * Names for firestore.
     * collection, sub-collection and field name for collection Route.
     */

    private const val ROUTE = "route"
    private const val FOLLOWERS = "followers"
    private const val RATING_AVR = "ratingAvr"
    private const val WALKERS = "walkers"
    private const val PHOTO_POINTS = "photoPoints"

    private const val COMMENTS = "comments"
    private const val RATINGS = "ratings"

    /**
     * Names for firestore.
     * collection, sub-collection and field name for collection Event.
     */

    private const val EVENT = "event"
    private const val INVITED = "invited"
    private const val MEMBER = "member"
    private const val MEMBER_COUNT = "memberCount"

    /**
     * Names for firestore.
     * collection, sub-collection and field name for collection User.
     */

    private const val USER = "user"
    private const val ID = "id"
    private const val ID_CUSTOM = "idCustom"
    private const val ACCU_HOUR = "accumulatedHour"
    private const val ACCU_KM = "accumulatedKm"
    private const val WEATHER = "weather"
    private const val MEAL = "meal"

    private const val FRIENDS = "friends"
    private const val WALKS = "walks"


    /**
     * query for collection Route in firestore.
     */

    override suspend fun getAllRoute(): Result<List<Route>> {
        return Route().getListResultFrom(
            db.collection(ROUTE).get()
        )
    }

    override suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>> {
        return Route().getListResultFrom(
            db.collection(ROUTE).whereArrayContains(FOLLOWERS, userId).get()
        )
    }

    override suspend fun getUserRoutes(userId: String): Result<List<Route>> {
        return Route().getListResultFrom(
            db.collection(ROUTE).whereArrayContains(WALKERS, userId).get()
        )
    }

    /**
     * update query for sub-collection, field in Route in firestore.
     * Return boolean for result.
     */

    override suspend fun updateRouteRating(
        rating: RouteRating,
        route: Route,
        userId: String,
        comment: Comment?
    ): Result<Boolean> {

        val walkersNew =
            if (route.walkers.contains(userId)) route.walkers
            else route.walkers.plus(userId)

        val document = db.collection(ROUTE).document(route.id.toString())

        val list = mutableListOf<Result<Boolean>>()

        document.collection(RATINGS).get().apply {

            list.add(this.missionSuccessReturn(true))

            val ratings = RouteRating().getListResultFrom(this)

            if (ratings is Result.Success) {
                val ratingAvrNew = route.ratingAvr?.addToAverage(
                    rating, ratings.data.size
                ) as RouteRating

                document.update(RATING_AVR, ratingAvrNew.toHashMap(), WALKERS, walkersNew)
                    .missionSuccessReturn(true).apply {
                        list.add(this)
                    }
            }
        }

        val updateRatings = document.collection(RATINGS).add(rating.toHashMapInt())
            .missionSuccessReturn(true).apply {
                list.add(this)
            }

        if (comment != null) {
            document.collection(COMMENTS).add(comment).missionSuccessReturn(true).apply {
                list.add(this)
            }
        }
        return list.find { it !is Result.Success } ?: updateRatings
    }

    override suspend fun uploadPhotoPoints(
        routeId: String,
        photoPoints: List<PhotoPoint>
    ): Result<Boolean> {
        var position = 0
        val list = mutableListOf<Result<Boolean>>()
        for (item in photoPoints) {
            val baos = ByteArrayOutputStream()

            val bitmap = BitmapFactory.decodeFile(item.photo)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)

            val data = baos.toByteArray()

            position++

            val pathString = "images/$routeId/photo${position}.jpg"
            val routesImageRef = storageRef.child(pathString)

            val uploadPhoto = routesImageRef.putBytes(data).missionSuccessReturn(true)
            list.add(uploadPhoto)

            val newItem = PhotoPoint(point = item.point, photo = pathString)
            if (uploadPhoto is Result.Success) {
                db.collection(ROUTE).document(routeId).collection(PHOTO_POINTS).add(newItem).apply {
                    list.add(missionSuccessReturn(true))
                }
            }

        }
        return list.find { it !is Result.Success } ?: list[0]
    }


    override suspend fun createRouteByUser(route: Route): Result<Boolean> {
        val document = db.collection(ROUTE).document(route.id.toString())

        val taskCreateRoute = document.set(route.toHashMap()).missionSuccessReturn(true)

        val taskFirstRating = document.collection(RATINGS)
            .add(requireNotNull(route.ratingAvr?.toHashMapInt())).missionSuccessReturn(true)

        val taskFirstComment = document.collection(COMMENTS)
            .add(requireNotNull(route.comments[0].toHashMap())).missionSuccessReturn(true)

        return if (taskCreateRoute is Result.Success) {
            if (taskFirstRating is Result.Success) taskFirstComment else taskFirstRating
        } else {
            taskCreateRoute
        }
    }

    override suspend fun addUserToFollowers(userId: String, route: Route): Result<Boolean> {
        val list = route.followers as MutableList<String>
        list.add(userId)
        return db.collection(ROUTE).document(route.id.toString()).update(FOLLOWERS, list)
            .missionSuccessReturn(true)
    }

    override suspend fun removeUserFromFollowers(userId: String, route: Route): Result<Boolean> {
        val list = route.followers as MutableList<String>
        list.remove(userId)
        return db.collection(ROUTE).document(route.id.toString()).update(FOLLOWERS, list)
            .missionSuccessReturn(true)
    }

    /**
     * query for sub-collection, field in Route in firestore.
     */

    override suspend fun downloadPhotoPoints(routeId: String): Result<List<PhotoPoint>> {
        return PhotoPoint().getListResultFrom(
            db.collection(ROUTE).document(routeId).collection(PHOTO_POINTS).get()
        )
    }

    override suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String> =
        suspendCoroutine { continuation ->
            val baos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            val data = baos.toByteArray()

            val pathString = "images/$routeId.jpg"

            val routesImageRef = storageRef.child(pathString)

            routesImageRef.putBytes(data).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                routesImageRef.downloadUrl

            }.addOnCompleteListener { taskUri ->
                if (taskUri.isSuccessful) {
                    continuation.resume(Result.Success(pathString))
                } else {
                    taskUri.exception?.let {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }
            }
        }

    override suspend fun getRouteComments(routeId: String): Result<List<Comment>> {
        return Comment().getListResultFrom(
            db.collection(ROUTE).document(routeId).collection(COMMENTS).get()
        )
    }


    /**
     * query for collection Event in firestore.
     * Return list of Events
     */

    override suspend fun getAllEvents(): Result<List<Event>> {
        return Event().getListResultFrom(
            db.collection(EVENT).get()
        )
    }

    override suspend fun getPublicEvents(): Result<List<Event>> {
        return Event().getListResultFrom(
            db.collection(EVENT)
                .whereGreaterThanOrEqualTo("endDate", now())
                .whereEqualTo("public", true).get()
        )
    }

    override suspend fun getNowAndFutureEvents(): Result<List<Event>> {
        return Event().getListResultFrom(
            db.collection(EVENT).whereGreaterThanOrEqualTo("endDate", now()).get()
        )
    }

    override suspend fun getUserInvitation(userId: String): Result<List<Event>> {
        return Event().getListResultFrom(
            db.collection(EVENT).whereArrayContains("invited", userId)
                .whereGreaterThanOrEqualTo("endDate", now()).get()
        )
    }

    /**
     * update query for sub-collection, field in Event in firestore.
     * Return boolean for result.
     */

    override suspend fun createEvent(event: Event): Result<Boolean> {
        return db.collection(EVENT)
            .document(requireNotNull(event.id)).set(event).missionSuccessReturn(true)
    }

    override suspend fun joinEvent(user: User, event: Event): Result<Boolean> {
        return db.collection(EVENT).document(requireNotNull(event.id)).update(
            INVITED, FieldValue.arrayRemove(user.id)
            , MEMBER, FieldValue.arrayUnion(user.toFriend())
            , MEMBER_COUNT, event.memberCount?.plus(1)
        ).missionSuccessReturn(true)
    }

    override suspend fun joinPublicEvent(user: User, event: Event): Result<Boolean> {
        return db.collection(EVENT).document(requireNotNull(event.id)).update(
            MEMBER, FieldValue.arrayUnion(user.toFriend())
            , MEMBER_COUNT, event.memberCount?.plus(1)
        ).missionSuccessReturn(true)
    }


    /**
     * query for user authentication.
     */

    override suspend fun firebaseAuthWithGoogle(idToken: String?): Result<FirebaseUser> =
        suspendCoroutine { continuation ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        continuation.resume(Result.Success(requireNotNull(auth.currentUser)))
                    } else {
                        task.exception?.let {
                            Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                    }
                }
        }

    /**
     * query for collection User in firestore.
     */

    override suspend fun getUser(userId: String): Result<User?> {
        return User().getResultFrom(
            db.collection(USER).whereEqualTo(ID, userId).get()
        )
    }

    override suspend fun searchFriendWithId(idCustom: String): Result<User?> {
        return User().getResultFrom(
            db.collection(USER).whereEqualTo(ID_CUSTOM, idCustom).get()
        )
    }

    override suspend fun getFriendsById(ids: List<String>): Result<List<User>> {
        return User().getListResultFrom(
            db.collection(USER).whereIn("id", ids).get()
        )
    }

    /**
     * query for update or to check sub-collection, field in User in firestore.
     * Return boolean for result.
     */

    override suspend fun signUpUser(user: User): Result<Boolean> {
        return db.collection(USER).document(requireNotNull(user.id)).set(user)
            .missionSuccessReturn(true)
    }

    override suspend fun checkIdCustomBeenUsed(idCustom: String): Result<Boolean> {
        return db.collection(USER).whereEqualTo(ID_CUSTOM, idCustom).get()
            .querySuccessReturn(ifSuccess = false, ifEmpty = true)
    }

    override suspend fun updateWalks(walk: Walk, user: User): Result<Boolean> {
        val document = db.collection(USER).document(requireNotNull(user.id))

        val updateAccu = document.update(
            ACCU_HOUR,
            user.accumulatedHour?.addNewWalk(requireNotNull(walk.duration?.toFloat()?.div(60 * 60))),
            ACCU_KM,
            user.accumulatedKm?.addNewWalk(requireNotNull(walk.distance))
        ).missionSuccessReturn(true)

        val renewUser = document.get().apply {
            if (updateAccu is Result.Success) {
                val userRenew = User().getResultFrom(this)
                if (userRenew is Result.Success) {
                    UserManager.user = userRenew.data
                }
            }

        }.missionSuccessReturn(true)

        val renewWalk = document.collection(WALKS).add(walk).missionSuccessReturn(true)

        return if (updateAccu is Result.Success) {
            if (renewUser is Result.Success) renewWalk else renewUser
        } else {
            updateAccu
        }

    }

    override suspend fun addFriend(friend: Friend, user: User): Result<Boolean> {

        val taskMeAddYou = db.collection(USER).document(requireNotNull(user.id))
            .collection(FRIENDS).add(friend).missionSuccessReturn(true)

        val taskYouAddMe =
            db.collection(USER).document(requireNotNull(friend.id)).collection(FRIENDS)
                .add(user.toFriend()).missionSuccessReturn(true)

        return if (taskMeAddYou is Result.Success) taskYouAddMe else taskMeAddYou
    }

    override suspend fun checkFriendAdded(idCustom: String, userId: String): Result<Boolean> {
        return db.collection(USER).document(userId).collection(FRIENDS)
            .whereEqualTo(ID_CUSTOM, idCustom).get()
            .querySuccessReturn(ifSuccess = true, ifEmpty = false)
    }

    override suspend fun updateWeatherNotification(
        activate: Boolean, userId: String
    ): Result<Boolean> {
        return db.collection(USER).document(userId).update(WEATHER, activate)
            .missionSuccessReturn(activate)

    }

    override suspend fun updateMealNotification(
        activate: Boolean, userId: String
    ): Result<Boolean> {
        return db.collection(USER).document(userId).update(MEAL, activate)
            .missionSuccessReturn(activate)
    }

    override suspend fun updateUserAccumulated(user: User?, type: FrequencyType): Result<Boolean> {
        return if (user == null) {
            Result.Fail(getString(R.string.no_internet))
        } else {

            db.collection(USER).document(requireNotNull(user.id)).update(
                ACCU_KM, user.accumulatedKm?.updateByFrequency(type),
                ACCU_HOUR, user.accumulatedHour?.updateByFrequency(type)
            ).missionSuccessReturn(true)
        }
    }

    /**
     * query for sub-collection, field in User in firestore.
     */

    override suspend fun getUserFriendSimple(userId: String): Result<List<Friend>> {
        return Friend().getListResultFrom(
            db.collection(USER).document(userId).collection(FRIENDS).get()
        )
    }

    override suspend fun getUserWalks(userId: String): Result<List<Walk>> {
        return Walk().getListResultFrom(
            db.collection(USER).document(userId).collection(WALKS).get()
        )
    }

    override suspend fun getUserLatestWalk(userId: String): Result<Walk?> {
        return Walk().getResultFrom(
            db.collection(USER).document(userId).collection(WALKS)
                .orderBy("startTime", Query.Direction.DESCENDING).limit(1).get()
        )
    }

    override suspend fun getMemberWalks(
        eventStartTime: Timestamp,
        memberId: String
    ): Result<List<Walk>> {
        return Walk().getListResultFrom(
            db.collection(USER).document(memberId).collection(WALKS)
                .whereGreaterThanOrEqualTo("endTime", eventStartTime).get()
        )
    }

    /**
     * query for location or direction on map.
     */

    override suspend fun drawPath(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng>
    ): Result<DirectionResult> {

        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.no_internet))
        }

        return try {
            val result = WalkableApi.retrofitService.drawPath(
                origin.toQuery(),
                destination.toQuery(),
                waypoints = waypoints.toQuery()
            )

            result.error?.let {
                return Result.Fail(it)
            }

            Result.Success(result)
        } catch (e: Exception) {
            Logger.d("JJ [${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }

    }

    override suspend fun getUserCurrentLocation(): Result<Location?> =
        suspendCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.d("JJ_location success ${task.result}")
                    Logger.d("JJ_location success ${task.result?.latitude}")
                    continuation.resume(Result.Success(task.result))

                } else {
                    task.exception?.let {
                        Logger.d("JJ_location error ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }

            }
        }

    /**
     * query for weather.
     */

    override suspend fun getWeather(currentLocation: LatLng): Result<WeatherResult> {
        if (!isInternetConnected()) {
            return Result.Fail(getString(R.string.no_internet))
        }

        return try {
            val result = WeatherApi.retrofitServices.getWeather(
                lat = currentLocation.latitude,
                lon = currentLocation.longitude
            )

            result.error?.let {
                return Result.Fail(it)
            }

            Result.Success(result)
        } catch (e: Exception) {
            Logger.d("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }
}
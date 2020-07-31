package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tw.com.walkablecity.Logger
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.isInternetConnected
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.ext.*
import tw.com.walkablecity.network.WalkableApi
import tw.com.walkablecity.network.WeatherApi
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object WalkableRemoteDataSource: WalkableDataSource{
    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    private const val ROUTE = "route"
    private const val FOLLOWERS = "followers"
    private const val RATINGAVR = "ratingAvr"
    private const val WALKERS = "walkers"
    private const val COMMENTS = "comments"
    private const val FRIENDS = "friends"
    private const val EVENT = "event"
    private const val WALKS = "walks"
    private const val USER = "user"
    private const val RATINGS = "ratings"
    private const val INVITED = "invited"
    private const val MEMBER = "member"
    private const val MEMBER_COUNT = "memberCount"
    private const val ID = "id"
    private const val ID_CUSTOM = "idCustom"
    private const val ACCU_HOUR = "accumulatedHour"
    private const val ACCU_KM = "accumulatedKm"
    private const val WEATHER = "weather"
    private const val MEAL = "meal"
    private val fusedLocationClient = FusedLocationProviderClient(WalkableApp.instance)
    private val auth = Firebase.auth

    override suspend fun getUser(userId: String): Result<User?> = suspendCoroutine{continuation->
        db.collection(USER).whereEqualTo(ID,userId).get().addOnCompleteListener {task->
            if(task.isSuccessful){

                if(task.result == null || task.result!!.isEmpty) continuation.resume(Result.Success(null))
                else continuation.resume(Result.Success(task.result!!.toObjects(User::class.java)[0]))

            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserEvents(userId: String): Result<List<Event>> = suspendCoroutine{continuation->
        db.collection(EVENT).get().addOnCompleteListener {task->
            if(task.isSuccessful){

                if(task.result == null || task.result!!.isEmpty) continuation.resume(Result.Success(listOf()))
                else {
                    val list = task.result!!.toObjects(Event::class.java).filter{event->
                        event.member.any{friend->
                            friend.id == userId
                        }
                    }

                    continuation.resume(Result.Success(list))
                }

            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserFriends(userId: String): Result<List<User>> = suspendCoroutine { continuation->
        db.collection(USER).document(userId).collection(FRIENDS).get().continueWithTask {task->
            if(!task.isSuccessful){
                when(val exception = task.exception) {
                    null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                    else -> continuation.resume(Result.Error(exception))
                }
            }

            db.collection(USER).whereIn("id", task.result!!.toObjects(Friend::class.java).map{it.id}).get()
        }.addOnCompleteListener {task->
            if(task.isSuccessful){

                if(task.result == null || task.result!!.isEmpty) continuation.resume(Result.Success(listOf()))
                else continuation.resume(Result.Success(task.result!!.toObjects(User::class.java)))

            }else{
                when(val exception = task.exception) {
                    null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                    else -> {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${exception.message}")
                        continuation.resume(Result.Error(exception))
                    }
                }
            }
        }
    }

    override suspend fun getUserFriendSimple(userId: String): Result<List<Friend>> = suspendCoroutine{continuation->
        db.collection(USER).document(userId).collection(FRIENDS).get().addOnCompleteListener {task->
            if(task.isSuccessful){

                if(task.result == null || task.result!!.isEmpty) continuation.resume(Result.Success(listOf()))
                else continuation.resume(Result.Success(task.result!!.toObjects(Friend::class.java)))

            }else{
                when(val exception = task.exception) {
                    null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                    else -> {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${exception.message}")
                        continuation.resume(Result.Error(exception))
                    }
                }
            }
        }
    }

    override suspend fun getUserWalks(userId: String): Result<List<Walk>> = suspendCoroutine{ continuation->
        db.collection(USER).document(userId).collection(WALKS).get().addOnCompleteListener {task->
            if(task.isSuccessful){

                if(task.result == null || task.result!!.isEmpty) continuation.resume(Result.Success(listOf()))
                else continuation.resume(Result.Success(task.result!!.toObjects(Walk::class.java)))

            }else{
                when(val exception = task.exception) {
                    null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                    else -> {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${exception.message}")
                        continuation.resume(Result.Error(exception))
                    }
                }
            }
        }
    }

    override suspend fun getUserLatestWalk(userId: String): Result<Walk?> = suspendCoroutine{continuation->
        db.collection(USER).document(userId).collection(WALKS).orderBy("startTime", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener {task->
            if(task.isSuccessful){

                if(task.result == null || task.result!!.isEmpty) continuation.resume(Result.Success(null))
                else continuation.resume(Result.Success(task.result!!.toObjects(Walk::class.java)[0]))

            }else{
                when(val exception = task.exception) {
                    null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                    else -> {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${exception.message}")
                        continuation.resume(Result.Error(exception))
                    }
                }
            }
        }
    }

    override suspend fun checkFriendAdded(idCustom: String, userId: String): Result<Boolean> = suspendCoroutine{ continuation->
        db.collection(USER).document(userId).collection(FRIENDS).whereEqualTo(ID_CUSTOM,idCustom).get().addOnCompleteListener {task->
            if(task.isSuccessful){

                if(task.result == null || task.result!!.isEmpty) continuation.resume(Result.Success(false))
                else continuation.resume(Result.Success(true))

            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun searchFriendWithId(idCustom: String): Result<Friend?> = suspendCoroutine{ continuation ->

        db.collection(USER).whereEqualTo(ID_CUSTOM,idCustom).get().addOnCompleteListener {task->
            if(task.isSuccessful){
                if(task.result == null ||task.result!!.isEmpty){
                    continuation.resume(Result.Success(null))
                }else{
                    val friend = task.result!!.toObjects(User::class.java)[0].toFriend()
                    continuation.resume(Result.Success(friend))
                }

            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun addFriend(friend: Friend, user: User): Result<Boolean> = suspendCoroutine{continuation->

        var missionToComplete = 2

        db.collection(USER).document(requireNotNull(user.id)).collection(FRIENDS).add(friend).addOnCompleteListener{ task->
            if(task.isSuccessful){

                missionToComplete -= 1

                if(missionToComplete == 0)continuation.resume(Result.Success(true))

            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }

        db.collection(USER).document(requireNotNull(friend.id)).collection(FRIENDS).add(user.toFriend()).addOnCompleteListener{ task->
            if(task.isSuccessful){

                missionToComplete -= 1
                if(missionToComplete == 0) continuation.resume(Result.Success(true))

            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun checkIdCustomBeenUsed(idCustom: String): Result<Boolean> = suspendCoroutine{ continuation->
        db.collection(USER).whereEqualTo(ID_CUSTOM,idCustom).get().addOnCompleteListener {task->
            if(task.isSuccessful){
                if (task.result == null|| task.result!!.isEmpty) continuation.resume(Result.Success(true))
                else continuation.resume(Result.Success(false))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun firebaseAuthWithGoogle(idToken: String?): Result<FirebaseUser> = suspendCoroutine{ continuation ->
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){

                    continuation.resume(Result.Success(requireNotNull(auth.currentUser)))
                }else{
                    task.exception?.let{
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }
            }
    }

    override suspend fun signUpUser(user: User): Result<User> = suspendCoroutine {continuation ->
        db.collection(USER).document(requireNotNull(user.id)).set(user).addOnCompleteListener { task->
            if(task.isSuccessful){

                continuation.resume(Result.Success(user))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getAllRoute(): Result<List<Route>> = suspendCoroutine { continuation ->
        db.collection(ROUTE).get().addOnCompleteListener {task ->
            if(task.isSuccessful){
                val list = mutableListOf<Route>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" +document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java).apply {

                    }
                    list.add(route)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getRoutesRanking(
        sorting: RouteSorting,
        timeMin: Int,
        timeMax: Int
    ): Result<List<Route>>  = suspendCoroutine {continuation ->
        db.collection(ROUTE).get().addOnCompleteListener {task ->
            if(task.isSuccessful){
                val list = mutableListOf<Route>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" + document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java).apply {

                    }
                    list.add(route)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserFavoriteRoutes(userId: String): Result<List<Route>> = suspendCoroutine{ continuation ->
        db.collection(ROUTE).whereArrayContains(FOLLOWERS,userId).get().addOnCompleteListener {task ->
            if(task.isSuccessful){
                val list = mutableListOf<Route>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" + document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java).apply {

                    }
                    list.add(route)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserRoutes(userId: String): Result<List<Route>> = suspendCoroutine{continuation ->
        db.collection(ROUTE).whereArrayContains(WALKERS,userId).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val list = mutableListOf<Route>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" +document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java)
                    list.add(route)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getRoutesNearby(userLocation: LatLng): Result<List<Route>> = suspendCoroutine{continuation ->
        db.collection(ROUTE).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val list = mutableListOf<Route>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" +document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java)

                    for(point in route.waypoints){
                        val distance = userLocation.toLocation().distanceTo(point.toLocation())
                        if(distance<500){
                            list.add(route)
                            break
                        }
                    }

                }
                Logger.d("list $list")
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserCurrentLocation(): Result<LatLng> = suspendCoroutine {continuation ->
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if(task.isSuccessful){
                Logger.d("JJ_location success ${task.result}")
                Logger.d("JJ_location success ${task.result?.latitude}")
                continuation.resume(Result.Success(LatLng(task.result?.latitude ?: 0.0, task.result?.longitude?: 0.0)))

            }else{
                task.exception?.let{
                    Logger.d("JJ_location error ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }

        }
    }

    override suspend fun updateWalks(walk: Walk, user: User): Result<Boolean> = suspendCoroutine{continuation->
        var missionToComplete = 2

        db.collection(USER).document(requireNotNull(user.id)).apply{



            update(ACCU_HOUR, user.accumulatedHour?.addNewWalk(requireNotNull(walk.duration?.toFloat()?.div(60 * 60))),
                    ACCU_KM, user.accumulatedKm?.addNewWalk(requireNotNull(walk.distance))).continueWithTask { task->

                if(!task.isSuccessful){
                    if(task.exception !=null){
                        continuation.resume(Result.Error(task.exception!!))
                    }else{
                        continuation.resume(Result.Fail(getString(R.string.not_here)))
                    }
                }

                get()
            }.addOnCompleteListener {task->
                if(task.isSuccessful){
                    missionToComplete -= 1
                    UserManager.user = task.result!!.toObject(User::class.java)
                    if(missionToComplete == 0)continuation.resume(Result.Success(true))
                }else{
                    task.exception?.let{
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(getString(R.string.not_here)))
                }
            }

            collection(WALKS).add(walk).addOnCompleteListener {task->
                if(task.isSuccessful){

                    missionToComplete -= 1
                    if(missionToComplete == 0)continuation.resume(Result.Success(true))

                }else{
                    task.exception?.let{
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(getString(R.string.not_here)))
                }
            }
        }
    }

    override suspend fun updateRouteRating(rating: RouteRating, route: Route, userId: String): Result<Boolean> = suspendCoroutine { continuation ->
        val ratingToUpdate = rating.toHashMapInt()
        val walkersNew =
            if(route.walkers.contains(userId))route.walkers
            else route.walkers.plus(userId)
        val ratingAvrNew = route.ratingAvr?.addToAverage(rating, route) as RouteRating
        Logger.d("JJ_fire ratingAvr new $ratingAvrNew")
        var missionToComplete = 2

        db.collection(ROUTE).document(route.id.toString()).apply{
            update(RATINGAVR, ratingAvrNew.toHashMap(), WALKERS, walkersNew).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    missionToComplete -= 1
                    if(missionToComplete == 0) continuation.resume(Result.Success(true))
                }else{
                    task.exception?.let{
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }
            }
            collection(RATINGS).add(ratingToUpdate).addOnCompleteListener {task->
                if(task.isSuccessful){
                    missionToComplete -= 1
                    if(missionToComplete == 0) continuation.resume(Result.Success(true))
                }else{
                    task.exception?.let{
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }
            }
        }
    }

    override suspend fun getRouteMapImageUrl(routeId: String, bitmap: Bitmap): Result<String> = suspendCoroutine{continuation->
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val pathString = "images/$routeId.jpg"
        val routesRef = storageRef.child("$routeId.jpg")
        val routesImageRef = storageRef.child(pathString)
        routesRef.name == routesImageRef.name // true
        routesRef.path == routesImageRef.path // false
        routesImageRef.putBytes(data).continueWithTask{task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            routesImageRef.downloadUrl

        }.addOnCompleteListener {taskUri->
            if(taskUri.isSuccessful){
                continuation.resume(Result.Success(pathString))
            }else{
                taskUri.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getRouteComments(routeId: String): Result<List<Comment>> = suspendCoroutine{continuation->
        db.collection(ROUTE).document(routeId).collection(COMMENTS).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val list = mutableListOf<Comment>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" +document.id + "=>" + document.data)
                    val comment = document.toObject(Comment::class.java)

                    list.add(comment)

                }
                Logger.d("list $list")
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun createRouteByUser(route: Route): Result<Boolean> = suspendCoroutine { continuation ->
        var missionToComplete = 3


        // While the file names are the same, the references point to different files


        db.collection(ROUTE).apply{

            document(route.id.toString()).set(route.toHashMap()).addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    missionToComplete -= 1
                    if(missionToComplete ==0) continuation.resume(Result.Success(true))
                }else{
                    task.exception?.let{
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }
            }

            document(route.id.toString()).collection(RATINGS)
                .add(requireNotNull(route.ratingAvr?.toHashMapInt())).addOnCompleteListener {task ->
                if(task.isSuccessful){
                    missionToComplete -= 1
                    if(missionToComplete ==0) continuation.resume(Result.Success(true))
                }else{
                    task.exception?.let{
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }
            }

            document(route.id.toString()).collection(COMMENTS)
                .add(requireNotNull(route.comments[0].toHashMap())).addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        missionToComplete -= 1
                        if(missionToComplete ==0) continuation.resume(Result.Success(true))
                    }else{
                        task.exception?.let{
                            Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                    }
                }


        }

    }

    override suspend fun uploadPhotoPoints(routeId: String, photoPoints: List<PhotoPoint>): Result<Boolean> = suspendCoroutine{continuation->
        var missionToComplete = photoPoints.size
        var position = 0
        for(item in photoPoints){
            val baos = ByteArrayOutputStream()
            val bitmap = BitmapFactory.decodeFile(item.photo)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val data = baos.toByteArray()
            position++
            val pathString = "images/$routeId/photo${position}.jpg"
            val routesImageRef = storageRef.child(pathString)

            routesImageRef.putBytes(data).continueWithTask{task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                val newItem = PhotoPoint(point = item.point, photo = pathString)
                db.collection(ROUTE).document(routeId).collection("photoPoints").add(newItem)

            }.addOnCompleteListener {taskUri->
                if(taskUri.isSuccessful){
                    missionToComplete -= 1
                    if(missionToComplete == 0) continuation.resume(Result.Success(true))
                }else{
                    taskUri.exception?.let{
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }
            }

        }

    }

    override suspend fun downloadPhotoPoints(routeId: String): Result<List<PhotoPoint>> = suspendCoroutine{continuation->
        db.collection(ROUTE).document(routeId).collection("photoPoints").get().addOnCompleteListener {task->
            if(task.isSuccessful){
                val list = mutableListOf<PhotoPoint>()
                for(document in task.result!!){
                    list.add(document.toObject(PhotoPoint::class.java))
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun addUserToFollowers(userId: String, route: Route): Result<Boolean> = suspendCoroutine{ continuation->
        val list = route.followers as MutableList<String>
        list.add(userId)
        db.collection(ROUTE).document(route.id.toString()).update(FOLLOWERS,list).addOnCompleteListener {task->
            if(task.isSuccessful){
                continuation.resume(Result.Success(true))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun removeUserFromFollowers(userId: String, route: Route): Result<Boolean> = suspendCoroutine{ continuation->

        val list = route.followers as MutableList<String>
        list.remove(userId)
        db.collection(ROUTE).document(route.id.toString()).update(FOLLOWERS,list).addOnCompleteListener {task->
            if(task.isSuccessful){
                continuation.resume(Result.Success(false))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getPopularEvents(): Result<List<Event>> = suspendCoroutine{continuation->

        db.collection(EVENT).whereGreaterThanOrEqualTo("endDate",now()).whereEqualTo("public",true).get().addOnCompleteListener { task->
            if(task.isSuccessful){
                val list = mutableListOf<Event>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" +document.id + "=>" + document.data)
                    val event = document.toObject(Event::class.java)
                    list.add(event)
                }
                list.sortByDescending { it.memberCount }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserChallenges(user: User): Result<List<Event>> = suspendCoroutine{continuation->
        db.collection(EVENT).whereGreaterThanOrEqualTo("endDate",now()).get().addOnCompleteListener { task->
            if(task.isSuccessful){
                val list = mutableListOf<Event>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" +document.id + "=>" + document.data)
                    val event = document.toObject(Event::class.java).apply {

                    }
                    if(event.member.find { it.id == user.id } != null) list.add(event)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserParticipateEvent(user: User): Result<List<Event>> = suspendCoroutine{continuation->
        db.collection(EVENT).whereArrayContains("member",user.toFriend()).whereLessThanOrEqualTo("startDate",now())
            .get().addOnCompleteListener { task->
            if(task.isSuccessful){
                val list = mutableListOf<Event>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" +document.id + "=>" + document.data)
                    val event = document.toObject(Event::class.java).apply {

                    }
                    list.add(event)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserInvitation(userId: String): Result<List<Event>> = suspendCoroutine{ continuation->
        db.collection(EVENT).whereArrayContains("invited",userId).whereGreaterThanOrEqualTo("endDate",now()).get().addOnCompleteListener { task->
            if(task.isSuccessful){
                val list = mutableListOf<Event>()
                for(document in task.result!!){
                    Logger.d("JJ_fire" +document.id + "=>" + document.data)
                    val event = document.toObject(Event::class.java).apply {

                    }
                    list.add(event)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun createEvent(event: Event): Result<Boolean> = suspendCoroutine{continuation->
        db.collection(EVENT).document(requireNotNull(event.id)).set(event).addOnCompleteListener {task->
            if(task.isSuccessful){
                continuation.resume(Result.Success(true))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun joinEvent(user: User, event: Event): Result<Boolean> = suspendCoroutine{continuation->
        db.collection(EVENT).document(requireNotNull(event.id))
            .update(INVITED, FieldValue.arrayRemove(user.id)
                , MEMBER, FieldValue.arrayUnion(user.toFriend())
                , MEMBER_COUNT, event.memberCount?.plus(1))
            .addOnCompleteListener {task->
                if(task.isSuccessful){

                    continuation.resume(Result.Success(true))
                }else{
                    val result = task.exception
                    if(result != null){
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${result.message}")
                        continuation.resume(Result.Error(result))
                    }else
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }


            }

    }

    override suspend fun joinPublicEvent(user: User, event: Event): Result<Boolean> = suspendCoroutine{continuation->
        db.collection(EVENT).document(requireNotNull(event.id))
            .update(MEMBER, FieldValue.arrayUnion(user.toFriend())
                , MEMBER_COUNT, event.memberCount?.plus(1))
            .addOnCompleteListener {task->
                if(task.isSuccessful){

                    continuation.resume(Result.Success(true))
                }else{
                    val result = task.exception
                    if(result != null){
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${result.message}")
                        continuation.resume(Result.Error(result))
                    }else
                        continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }


            }
    }

    override suspend fun getMemberWalkDistance(eventStartTime: Timestamp, memberId: String): Result<Float> = suspendCoroutine { continuation->



        db.collection(USER).document(memberId).collection(WALKS).whereGreaterThanOrEqualTo("endTime", eventStartTime).get().addOnCompleteListener { task->
            if(task.isSuccessful){
                var distanceResult = 0f
                for(walk in task.result!!.toObjects(Walk::class.java)){
                    distanceResult += requireNotNull(walk.distance)
                }
                continuation.resume(Result.Success(distanceResult))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }


    }

    override suspend fun getMemberWalkHours(eventStartTime: Timestamp, memberId: String): Result<Float> = suspendCoroutine {continuation->


        db.collection(USER).document(memberId).collection(WALKS).whereGreaterThanOrEqualTo("endTime", eventStartTime).get().addOnCompleteListener { task->
            if(task.isSuccessful){
                var hourResult = 0f
                for(walk in task.result!!.toObjects(Walk::class.java)){
                    hourResult += (requireNotNull(walk.duration).toFloat())
                    Logger.d("JJ_fire member id $memberId hours $hourResult")
                }


                continuation.resume(Result.Success(hourResult))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }



    }

    override suspend fun getMemberWalkFrequencyResult(
        eventStartTime: Timestamp,
        target: EventTarget,
        memberId: String
    ): Result<Float> = suspendCoroutine {continuation->

        db.collection(USER).document(memberId).get().addOnCompleteListener { task->
            if(task.isSuccessful){

                val user = task.result!!.toObject(User::class.java)
                val accumulation = if(target.distance == null) requireNotNull(user?.accumulatedHour)  // frequency_hour
                else requireNotNull(user?.accumulatedKm) // frequency_distance

                continuation.resume(Result.Success(
                    when(target.frequencyType){
                        FrequencyType.DAILY -> accumulation.daily
                        FrequencyType.WEEKLY -> accumulation.weekly
                        FrequencyType.MONTHLY -> accumulation.monthly
                        else -> 20200714f
                    }
                ))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }


    }

    override suspend fun updateDailyEvents(user: User?, eventList: List<Event>): Result<Boolean> = suspendCoroutine{continuation->
        if(user == null || eventList.isEmpty()){
            continuation.resume(Result.Fail(getString(R.string.no_internet)))
        }else{
            var missionToComplete = eventList.size + 1
            Logger.d("daily mission $missionToComplete")
            db.collection(USER).document(requireNotNull(user.id)).update(ACCU_KM, user.accumulatedKm?.dailyUpdate(),
                ACCU_HOUR, user.accumulatedHour?.dailyUpdate()).addOnCompleteListener{ task ->
                if(!task.isSuccessful){
                    when(task.exception){
                        null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                        else -> continuation.resume(Result.Error(task.exception!!))
                    }
                }else{
                    missionToComplete -= 1
                    Logger.d("daily user mission $missionToComplete")
                    if(missionToComplete == 0){
                       continuation.resume(Result.Success(true))
                   }
                }
            }

            for(event in eventList){
                val isBasedOnHr: Boolean = event.target?.hour != null
                val friend = event.member.find { it.id == user.id}
                val newList: List<MissionFQ> = if(isBasedOnHr) friend?.accomplishFQ?.plus((user.accumulatedHour?.daily ?: 0f).toMissionFQ()) ?: listOf()
                else friend?.accomplishFQ?.plus((user.accumulatedKm?.daily ?: 0f).toMissionFQ()) ?: listOf()

                db.collection(EVENT).document(requireNotNull(event.id)).update(
                    MEMBER, FieldValue.arrayRemove(friend), MEMBER, FieldValue.arrayUnion(user.toFriend(newList as MutableList<MissionFQ>))).addOnCompleteListener{ task ->
                    if(!task.isSuccessful){
                        when(task.exception){
                            null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                            else -> continuation.resume(Result.Error(task.exception!!))
                        }
                    }else{
                        missionToComplete -= 1
                        Logger.d("JJ_work daily mission $missionToComplete")

                        if(missionToComplete == 0){
                            continuation.resume(Result.Success(true))
                        }
                    }
                }

            }

        }
    }

    override suspend fun updateWeeklyEvents(user: User?, eventList: List<Event>): Result<Boolean> = suspendCoroutine{continuation->
        if(user == null || eventList.isEmpty()){
            continuation.resume(Result.Fail(getString(R.string.no_internet)))
        }else{
            var missionToComplete = eventList.size + 1
            Logger.d("weekly mission $missionToComplete")
            db.collection(USER).document(requireNotNull(user.id)).update(ACCU_KM, user.accumulatedKm?.weeklyUpdate(),
                ACCU_HOUR, user.accumulatedHour?.weeklyUpdate()).addOnCompleteListener{ task ->
                if(!task.isSuccessful){
                    when(task.exception){
                        null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                        else -> continuation.resume(Result.Error(task.exception!!))
                    }
                }else{
                    missionToComplete -= 1
                    Logger.d("weekly user mission $missionToComplete")
                    if(missionToComplete == 0){
                        continuation.resume(Result.Success(true))
                    }
                }
            }

            for(event in eventList){
                val isBasedOnHr: Boolean = event.target?.hour != null
                val friend = event.member.find { it.id == user.id}
                val newList: List<MissionFQ> = if(isBasedOnHr) friend?.accomplishFQ?.plus((user.accumulatedHour?.weekly ?: 0f).toMissionFQ()) ?: listOf()
                else friend?.accomplishFQ?.plus((user.accumulatedKm?.weekly ?: 0f).toMissionFQ()) ?: listOf()

                db.collection(EVENT).document(requireNotNull(event.id)).update(
                    MEMBER, FieldValue.arrayRemove(friend), MEMBER, FieldValue.arrayUnion(user.toFriend(newList as MutableList<MissionFQ>))).addOnCompleteListener{ task ->
                    if(!task.isSuccessful){
                        when(task.exception){
                            null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                            else -> continuation.resume(Result.Error(task.exception!!))
                        }
                    }else{
                        missionToComplete -= 1
                        Logger.d("JJ_work weekly event mission $missionToComplete")
                        if(missionToComplete == 0){
                            continuation.resume(Result.Success(true))
                        }
                    }
                }
            }
        }
    }

    override suspend fun updateMonthlyEvents(user: User?, eventList: List<Event>): Result<Boolean> = suspendCoroutine{continuation->
        if(user == null || eventList.isEmpty()){
            continuation.resume(Result.Fail(getString(R.string.no_internet)))
        }else{
            var missionToComplete = eventList.size + 1
            Logger.d("JJ_work monthly mission $missionToComplete")
            db.collection(USER).document(requireNotNull(user.id)).update(ACCU_KM, user.accumulatedKm?.monthlyUpdate(),
                ACCU_HOUR, user.accumulatedHour?.monthlyUpdate()).addOnCompleteListener{ task ->
                if(!task.isSuccessful){
                    when(task.exception){
                        null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                        else -> continuation.resume(Result.Error(task.exception!!))
                    }
                }else{
                    missionToComplete -= 1
                    Logger.d("JJ_work monthly user mission $missionToComplete")
                    if(missionToComplete == 0){
                        continuation.resume(Result.Success(true))
                    }
                }
            }

            for(event in eventList){
                val isBasedOnHr: Boolean = event.target?.hour != null
                val friend = event.member.find { it.id == user.id}
                val newList: List<MissionFQ> = if(isBasedOnHr) friend?.accomplishFQ?.plus((user.accumulatedHour?.monthly ?: 0f).toMissionFQ()) ?: listOf()
                else friend?.accomplishFQ?.plus((user.accumulatedKm?.monthly ?: 0f).toMissionFQ()) ?: listOf()

                db.collection(EVENT).document(requireNotNull(event.id)).update(
                    MEMBER, FieldValue.arrayRemove(friend), MEMBER, FieldValue.arrayUnion(user.toFriend(newList as MutableList<MissionFQ>))).addOnCompleteListener{ task ->
                    if(!task.isSuccessful){
                        when(task.exception){
                            null -> continuation.resume(Result.Fail(getString(R.string.not_here)))
                            else -> continuation.resume(Result.Error(task.exception!!))
                        }
                    }else{
                        missionToComplete -= 1
                        Logger.d("JJ_work monthly event mission $missionToComplete")
                        if(missionToComplete == 0){
                            continuation.resume(Result.Success(true))
                        }
                    }
                }
            }
        }
    }

    override suspend fun updateWeatherNotification(activate: Boolean, userId: String): Result<Boolean> = suspendCoroutine{continuation->
        db.collection(USER).document(userId).update(WEATHER, activate).addOnCompleteListener {task->
            if(task.isSuccessful){
                continuation.resume(Result.Success(activate))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(getString(R.string.not_here)))

            }

        }
    }

    override suspend fun updateMealNotification(activate: Boolean, userId: String): Result<Boolean> = suspendCoroutine{continuation->
        db.collection(USER).document(userId).update(MEAL, activate).addOnCompleteListener {task->
            if(task.isSuccessful){
                continuation.resume(Result.Success(activate))
            }else{
                task.exception?.let{
                    Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                    return@addOnCompleteListener
                }
                continuation.resume(Result.Fail(getString(R.string.not_here)))

            }

        }
    }

    //google map Api Zone

    override suspend fun drawPath(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng>
    ): Result<DirectionResult> {

        if(!isInternetConnected()){
            return Result.Fail(getString(R.string.no_internet))
        }

        return try{
            val result = WalkableApi.retrofitService.drawPath(origin.toQuery(), destination.toQuery(), waypoints = waypoints.toQuery())

            result.error?.let{
                return Result.Fail(it)
            }

            Result.Success(result)
        }catch(e: Exception){
            Logger.d("JJ [${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }

    }

    override suspend fun getRouteMapImage(
        center: GeoPoint,
        zoom: Int,
        path: List<GeoPoint>
    ): Result<MapImageResult> {
        if(!isInternetConnected()){
            return Result.Fail(getString(R.string.no_internet))
        }

        return try{
            val result = WalkableApi.retrofitService.getImage(center = center.toQuery(), zoom = zoom.toString(), path = path.toLatLngPoints().toQuery())

            result.error?.let{
                return Result.Fail(it)
            }

            Result.Success(result)
        }catch(e: Exception){
            Logger.d("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getWeather(currentLocation: LatLng): Result<WeatherResult> {
        if(!isInternetConnected()){
            return Result.Fail(getString(R.string.no_internet))
        }

        return try{
            val result = WeatherApi.retrofitServices.getWeather(lat = currentLocation.latitude, lon = currentLocation.longitude)

            result.error?.let{
                return Result.Fail(it)
            }

            Result.Success(result)
        }catch(e: Exception){
            Logger.d("[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }
}
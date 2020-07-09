package tw.com.walkablecity.data.source

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import retrofit2.http.Url
import tw.com.walkablecity.R
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.isInternetConnected
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.ext.*
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.network.WalkableApi
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URL
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
    private const val EVENT = "event"
    private const val USER = "user"
    private const val RATINGS = "ratings"
    private val fusedLocationClient = FusedLocationProviderClient(WalkableApp.instance)


    override suspend fun getAllRoute(): Result<List<Route>> = suspendCoroutine {continuation ->
        db.collection(ROUTE).get().addOnCompleteListener {task ->
            if(task.isSuccessful){
                val list = mutableListOf<Route>()
                for(document in task.result!!){
                    Log.d("JJ_fire",document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java).apply {

                    }
                    list.add(route)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
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
                    Log.d("JJ_fire",document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java).apply {

                    }
                    list.add(route)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserFavoriteRoutes(userId: Int): Result<List<Route>> = suspendCoroutine{ continuation ->
        db.collection(ROUTE).whereArrayContains(FOLLOWERS,userId).get().addOnCompleteListener {task ->
            if(task.isSuccessful){
                val list = mutableListOf<Route>()
                for(document in task.result!!){
                    Log.d("JJ_fire",document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java).apply {

                    }
                    list.add(route)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserRoutes(userId: Int): Result<List<Route>> = suspendCoroutine{continuation ->
        db.collection(ROUTE).whereArrayContains(WALKERS,userId).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val list = mutableListOf<Route>()
                for(document in task.result!!){
                    Log.d("JJ_fire",document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java)
                    list.add(route)
                }
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
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
                    Log.d("JJ_fire",document.id + "=>" + document.data)
                    val route = document.toObject(Route::class.java)

                    for(point in route.waypoints){
                        val distance = userLocation.toLocation().distanceTo(point.toLocation())
                        if(distance<500){
                            list.add(route)
                            break
                        }
                    }

                }
                Log.d("JJ","list $list")
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getUserCurrentLocation(): Result<LatLng> = suspendCoroutine {continuation ->
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if(task.isSuccessful){
                Log.d("JJ_location","success ${task.result}")
                Log.d("JJ_location","success ${task.result?.latitude}")
                continuation.resume(Result.Success(LatLng(task.result?.latitude ?: 0.0, task.result?.longitude?: 0.0)))

            }else{
                task.exception?.let{
                    Log.d("JJ_location","error ${it.message}")
                    continuation.resume(Result.Error(it))
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }

        }
    }

    override suspend fun updateRouteRating(rating: RouteRating, route: Route, userId: Int): Result<Boolean> = suspendCoroutine { continuation ->
        val ratingToUpdate = rating.toHashMapInt()
        val walkersNew =
            if(route.walkers.contains(userId))route.walkers
            else route.walkers.plus(userId)
        val ratingAvrNew = route.ratingAvr?.addToAverage(rating, route) as RouteRating
        Log.d("JJ_fire","ratingAvr new $ratingAvrNew")
        var missionToComplete = 2

        db.collection(ROUTE).document(route.id.toString()).apply{
            update(RATINGAVR, ratingAvrNew.toHashMap(), WALKERS, walkersNew).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    missionToComplete -= 1
                    if(missionToComplete == 0) continuation.resume(Result.Success(true))
                }else{
                    task.exception?.let{
                        Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
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
                        Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                }
            }
        }
    }

    override suspend fun getRouteMapImageUrl(routeId: Long, bitmap: Bitmap): Result<String> = suspendCoroutine{continuation->
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
                    Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
                continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
            }
        }
    }

    override suspend fun getRouteComments(routeId: Long): Result<List<Comment>> = suspendCoroutine{continuation->
        db.collection(ROUTE).document(routeId.toString()).collection(COMMENTS).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val list = mutableListOf<Comment>()
                for(document in task.result!!){
                    Log.d("JJ_fire",document.id + "=>" + document.data)
                    val comment = document.toObject(Comment::class.java)

                    list.add(comment)

                }
                Log.d("JJ","list $list")
                continuation.resume(Result.Success(list))
            }else{
                task.exception?.let{
                    Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
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
                        Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
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
                        Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
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
                            Log.d("JJ_fire","[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(WalkableApp.instance.getString(R.string.not_here)))
                    }
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
            Log.d("JJ","[${this::class.simpleName}] exception=${e.message}")
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
            Log.d("JJ","[${this::class.simpleName}] exception=${e.message}")
            Result.Error(e)
        }
    }
}
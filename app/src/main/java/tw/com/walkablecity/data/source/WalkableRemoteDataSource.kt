package tw.com.walkablecity.data.source

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import tw.com.walkablecity.R
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object WalkableRemoteDataSource: WalkableDataSource{
    private val db = Firebase.firestore
    private const val ROUTE = "route"
    private const val FOLLOWERS = "followers"
    private const val WALKERS = "walkers"
    private const val EVENT = "event"
    private const val USER = "user"
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

    override suspend fun getUserFavoriteRoutes(userId: Int): Result<List<Route>> = suspendCoroutine{continuation ->
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
}
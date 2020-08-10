package tw.com.walkablecity.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import tw.com.walkablecity.R
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.util.Util
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Helper function for remote data source from firestore.
 * Get class from firestore and handle with exceptions.
 */

suspend fun <T : Any> T.getResultFrom(source: Task<*>): Result<T?> =
    suspendCoroutine { continuation ->
        source.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                when(val result = task.result){
                    is QuerySnapshot ->{
                        if (result.isEmpty) {
                            continuation.resume(Result.Success(null))
                        } else {
                            continuation.resume(
                                Result.Success(result.toObjects(this::class.java)[0])
                            )
                        }
                    }
                    is DocumentSnapshot ->{
                        continuation.resume(Result.Success(result.toObject(this::class.java)))
                    }
                }

            } else {
                when (val exception = task.exception) {
                    null -> continuation.resume(
                        Result.Fail(Util.getString(R.string.not_here))
                    )
                    else -> {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${exception.message}")
                        continuation.resume(Result.Error(exception))
                    }
                }
            }
        }
    }

/**
 * Helper function for remote data source from firestore.
 * Get list from firestore and handle with exceptions.
 */

suspend fun <T : Any> T.getListResultFrom(source: Task<QuerySnapshot>): Result<List<T>> =
    suspendCoroutine { continuation ->
        source.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                if (task.result == null || task.result!!.isEmpty) {
                    continuation.resume(Result.Success(listOf()))
                } else {
                    continuation.resume(Result.Success(task.result!!.toObjects(this::class.java)))
                }

            } else {
                when (val exception = task.exception) {
                    null -> continuation.resume(Result.Fail(Util.getString(R.string.not_here)))

                    else -> {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${exception.message}")
                        continuation.resume(Result.Error(exception))
                    }
                }
            }
        }
    }

/**
 * Helper function for remote data source from firestore.
 * Get task success check from firestore and handle with exceptions.
 */

suspend fun Task<*>.missionSuccessReturn(ifSuccess: Boolean): Result<Boolean> =
    suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(Result.Success(ifSuccess))
            } else {
                when (val exception = task.exception) {
                    null -> continuation.resume(Result.Fail(Util.getString(R.string.not_here)))
                    else -> {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${exception.message}")
                        continuation.resume(Result.Error(exception))
                    }
                }
            }
        }
    }


/**
 * Helper function for remote data source from firestore.
 * Check query snapshot task success or not from firestore and handle with exceptions.
 */

suspend fun Task<QuerySnapshot>.querySuccessReturn(
    ifSuccess: Boolean,
    ifEmpty: Boolean
): Result<Boolean> =
    suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {

                if (task.result == null || task.result!!.isEmpty) {
                    continuation.resume(Result.Success(ifEmpty))
                } else {
                    continuation.resume(Result.Success(ifSuccess))
                }
            } else {
                when (val exception = task.exception) {
                    null -> continuation.resume(
                        Result.Fail(Util.getString(R.string.not_here))
                    )
                    else -> {
                        Logger.d("JJ_fire [${this::class.simpleName}] Error getting documents. ${exception.message}")
                        continuation.resume(Result.Error(exception))
                    }
                }
            }
        }
    }
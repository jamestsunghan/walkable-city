package tw.com.walkablecity.data.source

import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route

interface WalkableDataSource {
    suspend fun getAllRoute(): Result<List<Route>>
    suspend fun getUserFavoriteRoutes(userId: Int): Result<List<Route>>
    suspend fun getUserRoutes(userId: Int): Result<List<Route>>

}
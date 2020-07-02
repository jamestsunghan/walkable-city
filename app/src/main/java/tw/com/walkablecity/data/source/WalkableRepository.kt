package tw.com.walkablecity.data.source

import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route

interface WalkableRepository {
    suspend fun getAllRoute(): Result<List<Route>>
}
package tw.com.walkablecity.data.source

import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route

class DefaultWalkableRepository(private val remote: WalkableDataSource): WalkableRepository {
    override suspend fun getAllRoute(): Result<List<Route>> {
        return remote.getAllRoute()
    }

    override suspend fun getUserFavoriteRoutes(userId: Int): Result<List<Route>> {
        return remote.getUserFavoriteRoutes(userId)
    }

    override suspend fun getUserRoutes(userId: Int): Result<List<Route>> {
        return remote.getUserRoutes(userId)
    }
}
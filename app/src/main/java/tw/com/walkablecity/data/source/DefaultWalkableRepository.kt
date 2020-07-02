package tw.com.walkablecity.data.source

import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route

class DefaultWalkableRepository(private val remote: WalkableDataSource): WalkableRepository {
    override suspend fun getAllRoute(): Result<List<Route>> {
        return remote.getAllRoute()
    }
}
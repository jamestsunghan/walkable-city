package tw.com.walkablecity.data.source

import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.Route

object WalkableRemoteDataSource: WalkableDataSource{
    override suspend fun getAllRoute(): Result<List<Route>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
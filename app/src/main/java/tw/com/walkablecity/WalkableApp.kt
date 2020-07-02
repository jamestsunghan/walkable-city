package tw.com.walkablecity

import android.app.Application
import tw.com.walkablecity.data.source.DefaultWalkableRepository
import tw.com.walkablecity.data.source.WalkableRemoteDataSource
import kotlin.properties.Delegates

class WalkableApp: Application(){

    val repo = DefaultWalkableRepository(WalkableRemoteDataSource)

    companion object {
        var instance: WalkableApp by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
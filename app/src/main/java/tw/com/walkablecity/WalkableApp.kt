package tw.com.walkablecity

import android.app.Application
import tw.com.walkablecity.data.source.DefaultWalkableRepository
import kotlin.properties.Delegates

class WalkableApp: Application(){

    val repo = DefaultWalkableRepository()

    companion object {
        var instance: WalkableApp by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
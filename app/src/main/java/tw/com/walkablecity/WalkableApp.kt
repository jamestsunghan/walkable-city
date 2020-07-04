package tw.com.walkablecity

import android.app.Application
import com.google.firebase.FirebaseApp
import tw.com.walkablecity.data.source.DefaultWalkableRepository
import tw.com.walkablecity.data.source.WalkableRemoteDataSource
import kotlin.properties.Delegates

class WalkableApp: Application(){

    lateinit var repo: DefaultWalkableRepository

    companion object {
        var instance: WalkableApp by Delegates.notNull()

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        repo = DefaultWalkableRepository(WalkableRemoteDataSource)
    }
}
package tw.com.walkablecity

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
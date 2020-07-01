package tw.com.walkablecity.ext

import android.app.Activity
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.factory.ViewModelFactory

fun Activity.getVMFactory(): ViewModelFactory{
    val repo = (applicationContext as WalkableApp).repo
    return ViewModelFactory(repo)
}
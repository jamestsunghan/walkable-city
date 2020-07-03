package tw.com.walkablecity

import android.widget.Toast

object Util {

    fun getString(resourceId: Int): String{
        return WalkableApp.instance.getString(resourceId)
    }

    fun makeShortToast(resourceId: Int){
        Toast.makeText(WalkableApp.instance, getString(resourceId),Toast.LENGTH_SHORT).show()
    }
}
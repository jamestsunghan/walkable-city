package tw.com.walkablecity

import android.widget.Toast

object Util {

    fun getString(resourceId: Int): String{
        return WalkableApp.instance.getString(resourceId)
    }

    fun makeShortToast(resourceId: Int){
        Toast.makeText(WalkableApp.instance, getString(resourceId),Toast.LENGTH_SHORT).show()
    }

    fun lessThenTenPadStart(time: Long): String{
        return if(time < 10){
            time.toString().padStart(2,'0')
        }else time.toString()
    }
}
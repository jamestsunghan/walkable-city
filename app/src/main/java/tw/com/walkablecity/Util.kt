package tw.com.walkablecity

object Util {

    fun getString(resourceId: Int): String{
        return WalkableApp.instance.getString(resourceId)
    }
}
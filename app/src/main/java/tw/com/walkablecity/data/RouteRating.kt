package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import tw.com.walkablecity.ext.toNewAverage

@Parcelize
data class RouteRating (
    val coverage: Float = 0f,
    val rest: Float = 0f,
    val scenery: Float= 0f,
    val snack: Float= 0f,
    val tranquility: Float= 0f,
    val vibe: Float= 0f
): Parcelable{
    fun sum(): Float = coverage + rest + scenery + snack + tranquility + vibe

    fun average(): Float = sum().div(6)

    fun toHashMapInt(): HashMap<String, Int>{
        return hashMapOf(
            "coverage" to this.coverage.toInt(),
            "vibe" to this.vibe.toInt(),
            "snack" to this.snack.toInt(),
            "scenery" to this.scenery.toInt(),
            "rest" to this.rest.toInt(),
            "tranquility" to this.tranquility.toInt()
        )
    }

    fun toHashMap(): HashMap<String, Float>{
        return hashMapOf(
            "coverage" to this.coverage,
            "vibe" to this.vibe,
            "snack" to this.snack,
            "scenery" to this.scenery,
            "rest" to this.rest,
            "tranquility" to this.tranquility
        )
    }

    fun addToAverage(rating: RouteRating, route: Route, ratings: Int): RouteRating{

        return RouteRating(
            coverage    = this.coverage.toNewAverage(rating.coverage, route, ratings),
            rest        = this.rest.toNewAverage(rating.rest, route, ratings),
            snack       = this.snack.toNewAverage(rating.snack, route, ratings),
            scenery     = this.scenery.toNewAverage(rating.scenery, route, ratings),
            tranquility = this.tranquility.toNewAverage(rating.tranquility, route, ratings),
            vibe        = this.vibe.toNewAverage(rating.vibe, route, ratings)
        )
    }

    fun sortingBy(filter: RouteSorting): Float{
        return when (filter) {
            RouteSorting.SCENERY     -> scenery
            RouteSorting.SNACK       -> snack
            RouteSorting.REST        -> rest
            RouteSorting.TRANQUILITY -> tranquility
            RouteSorting.COVERAGE    -> coverage
            RouteSorting.VIBE        -> vibe
        }
    }
}
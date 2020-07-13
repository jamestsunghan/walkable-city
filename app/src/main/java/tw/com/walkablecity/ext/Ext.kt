package tw.com.walkablecity.ext

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

fun RouteRating?.toSortList(filter: RouteSorting?): List<String>{
    return if(this == null){
        listOf()
    }else{

        val originList = mutableListOf(
            Pair(getString(R.string.filter_coverage),this.coverage),
            Pair(getString(R.string.filter_tranquility),this.tranquility),
            Pair(getString(R.string.filter_scenery),this.scenery),
            Pair(getString(R.string.filter_rest),this.rest),
            Pair(getString(R.string.filter_snack),this.snack),
            Pair(getString(R.string.filter_vibe),this.vibe)).sortedBy {
            it.second
        }
        val headItem: Pair<String,Float> = when(filter){
            RouteSorting.VIBE-> originList.filter{it.first == getString(R.string.filter_vibe)}[0]
            RouteSorting.COVERAGE-> originList.filter{it.first == getString(R.string.filter_coverage)}[0]
            RouteSorting.SNACK -> originList.filter{it.first == getString(R.string.filter_snack)}[0]
            RouteSorting.REST-> originList.filter{it.first == getString(R.string.filter_rest)}[0]
            RouteSorting.SCENERY-> originList.filter{it.first == getString(R.string.filter_scenery)}[0]
            RouteSorting.TRANQUILITY->originList.filter{it.first == getString(R.string.filter_tranquility)}[0]
            null-> originList.last()
        }


        originList.minus(headItem).plus(headItem).asReversed().map{
            "${it.first} | ${it.second}"
        }

    }

}

fun List<LatLng>.toDistance(): Float{
    var distance = 0F
    if(this.size > 1){
        val list = this.subList(1,lastIndex)
        for(position in 0..list.lastIndex){
            val unit = Util.calculateDistance(list[position],this[position])
            distance += unit
        }
    }

    return distance
}

fun LatLng.toLocation(): Location{
    return Location("").apply{
        latitude = this@toLocation.latitude
        longitude = this@toLocation.longitude
    }
}

fun LatLng.toGeoPoint():GeoPoint{
    return GeoPoint(latitude,longitude)
}

fun GeoPoint.toLocation(): Location{
    return Location("").apply{
        latitude = this@toLocation.latitude
        longitude = this@toLocation.longitude
    }
}

fun List<GeoPoint>.toLatLngPoints(): List<LatLng>{
    return this.map{
        LatLng(it.latitude, it.longitude)
    }
}

fun List<LatLng>.toQuery():String{
    var query = StringBuilder()
    if(this.isNotEmpty()){
        query = query.append("${this[0].latitude},${this[0].longitude}")
        if(this.size > 2){
            for(position in 1 until this.lastIndex){
                query = query.append("|${this[position].latitude},${this[position].longitude}")
            }
        }
    }
    return query.toString()
}

fun LatLngResult.toQuery(): String{
    return "${this.lat},${this.lng}"
}

fun LatLng.toQuery(): String{
    return "${this.latitude},${this.longitude}"
}

fun GeoPoint.toQuery(): String{
    return "${this.latitude},${this.longitude}"
}

fun RouteRating.toHashMapInt(): HashMap<String, Int>{
    return hashMapOf(
        "coverage" to this.coverage.toInt(),
        "vibe" to this.vibe.toInt(),
        "snack" to this.snack.toInt(),
        "scenery" to this.scenery.toInt(),
        "rest" to this.rest.toInt(),
        "tranquility" to this.tranquility.toInt()
    )
}

fun RouteRating.toHashMap(): HashMap<String, Float>{
    return hashMapOf(
        "coverage" to this.coverage,
        "vibe" to this.vibe,
        "snack" to this.snack,
        "scenery" to this.scenery,
        "rest" to this.rest,
        "tranquility" to this.tranquility
    )
}

fun RouteRating.addToAverage(rating: RouteRating, route: Route): RouteRating{

    return RouteRating(
        coverage = this.coverage.toNewAverage(rating, route),
        rest = this.rest.toNewAverage(rating, route),
        snack = this.snack.toNewAverage(rating, route),
        scenery = this.scenery.toNewAverage(rating, route),
        tranquility = this.tranquility.toNewAverage(rating, route),
        vibe = this.vibe.toNewAverage(rating, route)
    )
}

fun Route.toHashMap(): HashMap<String,Any?>{
    return hashMapOf(
        "description" to this.description,
        "followers" to this.followers,
        "id" to this.id,
        "length" to this.length,
        "mapImage" to this.mapImage,
        "minutes" to this.minutes,
        "ratingAvr" to this.ratingAvr?.toHashMap(),
        "title" to this.title,
        "walkers" to this.walkers,
        "waypoints" to this.waypoints
    )
}

fun Float.toNewAverage(rating: RouteRating, route: Route): Float{
    return this.times(route.walkers.size).plus(rating.coverage).div(route.walkers.size + 1)
}

fun Comment.toHashMap(): HashMap<String, Any?>{
    return hashMapOf(
        "content" to this.content,
        "createTime" to this.createTime,
        "recommend" to this.recommend,
        "userId" to this.userId
    )
}

fun String.toComment(recommend: Int, userId: String): Comment{
    return Comment(
        content = this,
        createTime = now(),
        recommend = recommend,
        userId = userId
    )
}

fun Walk.toRouteId(userId: String): String{
    return "${userId}${this.startTime.toDateLong().times(100)}"
}

fun Timestamp.toDateLong(): Long{
    return SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN).format(this.seconds.times(1000)).toLong()
}

fun FirebaseUser.toSignInUser(idCustom: String?): User{
    return User(
        id = uid,
        idCustom = idCustom,
        name = displayName,
        picture = photoUrl.toString(),
        email = email,
        accumulatedKm = Accumulation(0f,0f,0f,0f,0f),
        accumulatedHour = Accumulation(0f,0f,0f,0f,0f),
        friends = listOf(),
        walks = listOf()
    )

}

fun User.toFriend(): Friend{
    return Friend(
        id = id,
        idCustom = idCustom,
        name = name,
        picture = picture,
        email = email
    )
}

fun Accumulation.addNewWalk(input: Float): Accumulation{
    return Accumulation(
        daily   = daily   + input,
        weekly  = weekly  + input,
        monthly = monthly + input,
        yearly  = yearly  + input,
        total   = total   + input
    )
}
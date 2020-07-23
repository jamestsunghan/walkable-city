package tw.com.walkablecity.ext

import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.location.Location
import android.net.Uri
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
import tw.com.walkablecity.eventdetail.MemberItem
import tw.com.walkablecity.profile.bestwalker.WalkerItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

fun Walk.toRouteId(userIdCustom: String): String{
    return "${userIdCustom}${this.startTime?.toDateLong()?.times(100)}"
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

fun User.toFriend(accomplished: MutableList<MissionFQ>): Friend{
    return Friend(
        id = id,
        idCustom = idCustom,
        name = name,
        picture = picture,
        email = email,
        accomplishFQ = accomplished
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

fun Accumulation.dailyUpdate(): Accumulation{
    return Accumulation(
        daily   = 0f,
        weekly  = weekly,
        monthly = monthly,
        yearly  = yearly,
        total   = total
    )
}

fun Accumulation.weeklyUpdate(): Accumulation{
    return Accumulation(
        daily   = 0f,
        weekly  = 0f,
        monthly = monthly,
        yearly  = yearly,
        total   = total
    )
}

fun Float.toMissionFQ(): MissionFQ{
    return MissionFQ(
        date = Timestamp(now().seconds - (60*60*12), now().nanoseconds),
        accomplish = this
    )
}

fun Accumulation.monthlyUpdate(): Accumulation{
    return Accumulation(
        daily   = 0f,
        weekly  = 0f,
        monthly = 0f,
        yearly  = yearly,
        total   = total
    )
}

fun List<User>.toWalkerItem(): List<WalkerItem>{
    return when(this.size > 3){
         true  ->{
             val top3 = this.slice(0..2)
             val theRest = this.slice(3..lastIndex)
             Log.d("JJ", "the rest ${theRest.size}")
             listOf(top3).map{WalkerItem.Tops(it)} + this.map{WalkerItem.Walkers(it)}
         }
         false -> {
             val theRest = this.slice(1..lastIndex)
             listOf(WalkerItem.Tops(listOf(this[0]))) + theRest.map{WalkerItem.Walkers(it)}
         }
    }
}

fun List<Friend>.toMemberItem(): List<MemberItem>{
    return listOf(MemberItem.Board) + this.map{MemberItem.Member(it)}
}

fun Bitmap.saveToInternalStorage(context: Context){


    val file = File(context.cacheDir,"images")

    try{
        file.mkdir()
        val stream = FileOutputStream("${file}/image.png")

        this.compress(Bitmap.CompressFormat.PNG, 100, stream)

        stream.flush()
        stream.close()
    }catch (e: IOException){
        e.printStackTrace()
    }

}

fun Bitmap.getCroppedBitmap(): Bitmap{
    val output = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val color = 0xff424242
    val paint = Paint()
    val rect = Rect(0,0,this.width, this.height)

    paint.isAntiAlias = true
    canvas.drawARGB(0,0,0,0)
    paint.color = color.toInt()
    canvas.drawCircle((this.width / 2).toFloat(), (this.height / 2).toFloat(), (this.height / 2).toFloat(), paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    return output

}
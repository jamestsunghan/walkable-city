package tw.com.walkablecity.ext

import android.content.Context
import android.graphics.*
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.util.Logger
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.directionresult.LatLngResult
import tw.com.walkablecity.eventdetail.MemberItem
import tw.com.walkablecity.profile.bestwalker.WalkerItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun RouteRating?.toSortList(filter: RouteSorting?): List<String> {
    return if (this == null) {
        listOf()
    } else {

        val originList = mutableListOf(
            Pair(RouteSorting.COVERAGE.title, this.coverage),
            Pair(RouteSorting.TRANQUILITY.title, this.tranquility),
            Pair(RouteSorting.SCENERY.title, this.scenery),
            Pair(RouteSorting.REST.title, this.rest),
            Pair(RouteSorting.SNACK.title, this.snack),
            Pair(RouteSorting.VIBE.title, this.vibe)
        ).sortedBy {
            it.second
        }
        val headItem: Pair<String, Float> =
            originList.find {pair->
                pair.first == filter?.title
            } ?: originList.last()

        originList.minus(headItem).plus(headItem).asReversed().map {pair->
            "${pair.first} | ${pair.second.times(10).roundToInt().toFloat().div(10)}"
        }

    }

}

fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)

fun List<LatLng>.toDistance(): Float {
    var distance = 0F
    if (this.size > 1) {
        val list = this.subList(1, lastIndex)
        for (position in 0..list.lastIndex) {
            val unit = Util.calculateDistance(list[position], this[position])
            distance += unit
        }
    }

    return distance
}

fun LatLng.toLocation(): Location {
    return Location("").apply {
        latitude = this@toLocation.latitude
        longitude = this@toLocation.longitude
    }
}

fun LatLng.toGeoPoint(): GeoPoint {
    return GeoPoint(latitude, longitude)
}

fun GeoPoint.toLocation(): Location {
    return Location("").apply {
        latitude  = this@toLocation.latitude
        longitude = this@toLocation.longitude
    }
}

fun List<GeoPoint>.toLatLngPoints(): List<LatLng> {
    return this.map {
        LatLng(it.latitude, it.longitude)
    }
}

fun List<LatLng>.toQuery(): String {
    var query = StringBuilder()
    if (this.isNotEmpty()) {
        query = query.append("${this[0].latitude},${this[0].longitude}")
        if (this.size > 2) {
            for (position in 1 until this.lastIndex) {
                query = query.append("|${this[position].latitude},${this[position].longitude}")
            }
        }
    }
    return query.toString()
}

fun LatLngResult.toQuery(): String {
    return "${this.lat},${this.lng}"
}

fun LatLng.toQuery(): String {
    return "${this.latitude},${this.longitude}"
}

fun GeoPoint.toQuery(): String {
    return "${this.latitude},${this.longitude}"
}

fun Float.toNewAverage(new: Float, ratings: Int): Float {
    return this.times(ratings).plus(new).div(ratings + 1)
}

fun String.toComment(recommend: Int, userId: String): Comment {
    return Comment(
        content = this,
        createTime = now(),
        recommend = recommend,
        userId = userId
    )
}

fun Timestamp.toDateLong(): Long {
    return SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN).format(this.seconds.times(1000))
        .toLong()
}

fun Timestamp.toDateString(): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.TAIWAN).format(this.seconds.times(1000))
}

fun FirebaseUser.toSignInUser(idCustom: String?): User {
    return User(
        id = uid,
        idCustom = idCustom,
        name = displayName,
        picture = photoUrl.toString(),
        email = email,
        accumulatedKm = Accumulation(0f, 0f, 0f, 0f, 0f),
        accumulatedHour = Accumulation(0f, 0f, 0f, 0f, 0f),
        friends = listOf(),
        walks = listOf()
    )

}

fun Float.toMissionFQ(): MissionFQ {
    return MissionFQ(
        date = Timestamp(now().seconds - (60 * 60 * 12), now().nanoseconds),
        accomplish = this
    )
}

fun List<User>.toWalkerItem(): List<WalkerItem> {
    return when (this.size > 3) {
        true -> {
            val top3 = this.slice(0..2)

            val theRest = this.slice(3..lastIndex)

            Logger.d("the rest ${theRest.size}")
            listOf(top3).map { list-> WalkerItem.Tops(list) } + this.map { user->
                WalkerItem.Walkers(user)
            }
        }
        false -> {
            val theRest = this.slice(1..lastIndex)

            listOf(WalkerItem.Tops(listOf(this[0]))) + theRest.map {user->
                WalkerItem.Walkers(user)
            }
        }
    }
}

fun List<Friend>.toMemberItem(): List<MemberItem> {
    return listOf(MemberItem.Board) + this.map { MemberItem.Member(it) }
}

fun Bitmap.saveToInternalStorage(context: Context) {


    val file = File(context.cacheDir, "images")

    try {
        file.mkdir()
        val stream = FileOutputStream("${file}/image.png")

        this.compress(Bitmap.CompressFormat.PNG, 100, stream)

        stream.flush()
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

}

fun Bitmap.getCroppedBitmap(): Bitmap {

    val output = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val color = 0xff424242
    val paint = Paint()
    val rect = Rect(0, 0, this.width, this.height)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color.toInt()
    canvas.drawCircle(
        (this.width / 2).toFloat(),
        (this.height / 2).toFloat(),
        (this.height / 2).toFloat(),
        paint
    )
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    return output

}

fun List<Route>.timeFilter(list: List<Float>, max: Float, filter: RouteSorting?): List<Route> {
    return this.filter {route->

        val range = list.sortedBy { time -> time }
        val topLimit = if (range[1] >= max) Float.MAX_VALUE else range[1]

        range[0] < route.minutes && route.minutes < topLimit

    }.sortedByDescending {route->
        if (filter == null) {
            route.ratingAvr?.average()
        } else {
            route.ratingAvr?.sortingBy(filter)
        }
    }
}

fun List<Route>.getNearBy(location: LatLng?): List<Route> {
    return location?.let {
        if (this.isNullOrEmpty()) {
            listOf()
        } else {
            this.filter { route ->

                route.waypoints.find { point ->
                    location.toLocation().distanceTo(point.toLocation()) < 500
                } != null

            }
        }
    } ?: listOf()
}

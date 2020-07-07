package tw.com.walkablecity.ext

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.LatLngResult
import tw.com.walkablecity.data.RouteRating
import tw.com.walkablecity.data.RouteSorting

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

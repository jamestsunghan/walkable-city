package tw.com.walkablecity.ext

import tw.com.walkablecity.R
import tw.com.walkablecity.Util
import tw.com.walkablecity.data.RouteRating

fun RouteRating?.toSortList(): List<String>{
    return if(this == null){
        listOf()
    }else{
        listOf(
            Pair(Util.getString(R.string.filter_coverage),this.coverage),
            Pair(Util.getString(R.string.filter_tranquility),this.tranquility),
            Pair(Util.getString(R.string.filter_scenery),this.scenery),
            Pair(Util.getString(R.string.filter_rest),this.rest),
            Pair(Util.getString(R.string.filter_snack),this.snack),
            Pair(Util.getString(R.string.filter_vibe),this.vibe)).sortedBy {
            it.second
        }.reversed().map{
            "${it.first} | ${it.second}"
        }
    }



}
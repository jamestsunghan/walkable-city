package tw.com.walkablecity


import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toPointF
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tw.com.walkablecity.Util.getColor
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.lessThenTenPadStart
import tw.com.walkablecity.data.*
import tw.com.walkablecity.detail.CommentAdapter
import tw.com.walkablecity.event.item.EventItemAdapter
import tw.com.walkablecity.favorite.FavoriteAdapter
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.loadroute.route.RouteItem
import tw.com.walkablecity.loadroute.route.RouteItemAdapter
import tw.com.walkablecity.ranking.RankingAdapter
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.roundToInt
import kotlin.math.sqrt


@BindingAdapter("pause")
fun walkPausing(view: TextView, status: WalkerStatus) {
    when (status) {
        WalkerStatus.PREPARE -> {
            view.visibility = View.GONE
        }
        WalkerStatus.WALKING -> {
            view.visibility = View.VISIBLE
            view.text = Util.getString(R.string.pause_walking)
        }
        WalkerStatus.PAUSING -> {
            view.visibility = View.VISIBLE
            view.text = Util.getString(R.string.resume_walking)
        }
        WalkerStatus.FINISH -> {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("routeItem")
fun addFilterAndSubmitList(view: RecyclerView, list: List<Route>?){
    val items = when(list){
        null -> listOf(RouteItem.Filter)
        else -> listOf(RouteItem.Filter) + list.map{RouteItem.LoadRoute(it)}
    }

    view.adapter.apply {
        when(this){
            is RouteItemAdapter -> submitList(items)
            is RankingAdapter -> submitList(items)
            is FavoriteAdapter -> submitList(items)
        }
    }

}

@BindingAdapter("comment")
fun bindComment(view: RecyclerView, list: List<Comment>?){
    list?.let{
        view.adapter.apply {
            when(this){
                is CommentAdapter -> submitList(it)

            }
        }
    }
}

@BindingAdapter("event")
fun bindEvent(view: RecyclerView, list: List<Event>?){
    list?.let{
        view.adapter.apply {
            when(this){
                is EventItemAdapter -> submitList(it)

            }
        }
    }
}

@BindingAdapter("eventType")
fun bindBackgroundWithEventType(layout: ConstraintLayout, type: EventType){
    val context = WalkableApp.instance
    layout.backgroundTintList = when(type){
        EventType.DISTANCE_GROUP -> context.getColorStateList(R.color.event_distance_group)
        EventType.DISTANCE_RACE  -> context.getColorStateList(R.color.event_distance_race)
        EventType.HOUR_GROUP     -> context.getColorStateList(R.color.event_hour_group)
        EventType.HOUR_RACE      -> context.getColorStateList(R.color.event_hour_race)
        EventType.FREQUENCY      -> context.getColorStateList(R.color.event_frequency)

    }
}

@BindingAdapter("target")
fun bindTextWithEventType(textView: TextView, type: EventType?){
    textView.text =  when(type){
        EventType.DISTANCE_GROUP -> getString(R.string.create_event_distance_title)
        EventType.DISTANCE_RACE  -> getString(R.string.create_event_distance_title)
        EventType.HOUR_GROUP     -> getString(R.string.create_event_hour_title)
        EventType.HOUR_RACE      -> getString(R.string.create_event_hour_title)
        EventType.FREQUENCY      -> getString(R.string.create_event_frequency_title)
        null                     -> getString(R.string.not_here)
    }
    if(type == null)textView.visibility = View.GONE
    else textView.visibility = View.VISIBLE
}

@BindingAdapter("typePosition")
fun bindTextWithEventTypePOsition(textView: TextView, position: Int?){

    textView.text = when(position){
        1 -> getString(R.string.create_event_frequency_distance)
        2 -> getString(R.string.create_event_frequency_hour)
        3 -> getString(R.string.create_event_frequency_distance)
        4 -> getString(R.string.create_event_frequency_distance)
        5 -> getString(R.string.create_event_frequency_hour)
        6 -> getString(R.string.create_event_frequency_hour)
        else -> getString(R.string.not_here)
    }
    if(position == null || position == 0 || position > 6)textView.visibility = View.GONE
    else textView.visibility = View.VISIBLE

}





@BindingAdapter("status", "route", "time")
fun walkerTimer(textView: TextView, status: WalkerStatus, mapRoute: GoogleRoute?, time: Long){
    val minutes = time / 60
    val seconds = time % 60
    val timeText = StringBuilder().append(lessThenTenPadStart(minutes))
        .append(":").append(lessThenTenPadStart(seconds)).toString()
    when(status){
        WalkerStatus.PREPARE -> {

            if(mapRoute == null){
                textView.text = getString(R.string.zero_time)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
            }else{
                var durationTotal = 0F
                for(item in mapRoute.legs){
                    durationTotal += item.duration?.value?.toFloat() ?: 0F
                }
                textView.text = String.format(getString(R.string.approximate_time), durationTotal / 60)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24F)
            }
        }
        WalkerStatus.WALKING -> {
            textView.text = timeText
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
        }
        WalkerStatus.PAUSING -> {
            textView.text = timeText
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
        }
        WalkerStatus.FINISH ->{
            textView.text = timeText
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
        }
    }
}

@BindingAdapter("status", "route","distance")
fun walkerDistance(textView: TextView, status: WalkerStatus, mapRoute: GoogleRoute?, distance: Float){
    when(status){
        WalkerStatus.PREPARE -> {
            if(mapRoute == null){
                textView.text = getString(R.string.zero_distance)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
            }else{
                var distanceTotal = 0F
                for(item in mapRoute.legs){
                    distanceTotal += item.distance?.value?.toFloat() ?: 0F
                }
                textView.text = String.format(getString(R.string.approximate_length), distanceTotal / 1000)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24F)
            }
        }
        else -> {
            textView.text = String.format(getString(R.string.recording_length),distance)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
        }

    }
}


@BindingAdapter("value")
fun convertInt2Float(slider: Slider, input: Int){
    slider.value = input.toFloat()
}

@InverseBindingAdapter(attribute = "value")
fun convertFloat2Int(slider: Slider):Int{
    return slider.value.toInt()
}

@BindingAdapter("valueAttrChanged")
fun setSliderListeners(slider: Slider, attrChange: InverseBindingListener){
    slider.addOnChangeListener { _, _, _ ->
        attrChange.onChange()
    }
}


@BindingAdapter("switch")
fun switchDrawable(imageView: ImageView, added: Boolean){
    when(added){
        true -> imageView.setImageResource(R.drawable.map_fav_24_added)
        false -> imageView.setImageResource(R.drawable.map_fav_24)
    }
}

@BindingAdapter("imageUrl")
fun glideImage(imageView: ImageView, url: String?){
    url?.let{
        val reference = Firebase.storage.reference.child(url)

        Log.d("JJ_ref","reference $reference")

        Glide.with(imageView.context)
            .load(reference).apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
            )
            .into(imageView)
    }

}

@BindingAdapter("date")
fun timeStampToDate(textView: TextView, time: Timestamp){
    textView.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time.seconds.times(1000))
}





@BindingAdapter("hexagon")
fun bgHexagonByColorCode(view: View, colorId: Int) {

    view.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {


            paint.color = getColor(colorId)

            paint.style = Paint.Style.FILL
//            val upPoint = Point((this.width/2).toInt(),0)
//            val downPoint = Point((this.width/2).toInt(),this.height.toInt())
//            val midPoint = Point((this.width/2).toInt(),(this.height/2).toInt())
//            val leftUpPoint = Point((this.width/2-(sqrt(3.0) *this.height/4)).toInt(),(this.height/4).toInt())
//            val leftDownPoint = Point((this.width/2-(sqrt(3.0) *this.height/4)).toInt(),(this.height*3/4).toInt())
//            val rightUpPoint = Point((this.width/2+(sqrt(3.0) *this.height/4)).toInt(),(this.height/4).toInt())
//            val rightDownPoint = Point((this.width/2+(sqrt(3.0) *this.height/4)).toInt(),(this.height*3/4).toInt())
            val path = Path()
            path.moveTo(this.width/2,0f)
            path.lineTo((this.width/2-(sqrt(3.0) *this.height/4)).toFloat(),this.height/4)
            path.lineTo((this.width/2-(sqrt(3.0) *this.height/4)).toFloat(),this.height*3/4)
            path.lineTo(this.width/2,this.height)
            path.lineTo((this.width/2+(sqrt(3.0) *this.height/4)).toFloat(),this.height*3/4)
            path.lineTo((this.width/2+(sqrt(3.0) *this.height/4)).toFloat(),this.height/4)
            path.close()
            canvas.drawPath(path,paint)

            paint.style = Paint.Style.STROKE

            paint.color = getColor(R.color.primaryDarkColor)
            paint.strokeWidth = WalkableApp.instance.resources
                .getDimensionPixelSize(R.dimen.hexagon_stroke).toFloat()
            canvas.drawLine(this.width/2,0f,this.width/2,this.height,paint)
            canvas.drawLine((this.width/2-(sqrt(3.0) *this.height/4)).toFloat(),this.height/4,(this.width/2+(sqrt(3.0) *this.height/4)).toFloat(),this.height*3/4,paint)
            canvas.drawLine((this.width/2-(sqrt(3.0) *this.height/4)).toFloat(),this.height*3/4,(this.width/2+(sqrt(3.0) *this.height/4)).toFloat(),this.height/4,paint)

        }
    })

}

@BindingAdapter("power")
fun hexagonByRating(view: View, rating: RouteRating) {

    view.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {


            paint.color = getColor(R.color.hexagon_fill)

            paint.style = Paint.Style.FILL

            val xLeft = (this.width/2-(sqrt(3.0) *this.height/4)).toFloat()
            val xRight = (this.width/2+(sqrt(3.0) *this.height/4)).toFloat()
            val yUp = (this.height/4)
            val yDown = (this.height*3/4)
            val xMid = (this.width/2)
            val yMid = (this.height/2)
//            val upPoint = Point(xMid,(yMid/5*(5-rating.coverage)).toInt())  //coverage, point1
//            val downPoint = Point(xMid,(yMid/5*(5+rating.rest)).toInt())  //rest,point4
//
//            val leftUpPoint = Point((xLeft+(xMid-xLeft)/5*(5-rating.tranquility)).toInt(),yUp+(yMid-yUp)/5*(5-list[1]))  // tranquility, point2
//            val leftDownPoint = Point(xLeft+(xMid-xLeft)/5*(5-list[2]),yDown-(yDown-yMid)/5*(5-list[2]))  // snack, point3
//            val rightUpPoint = Point(xRight-(xRight-xMid)/5*(5-list[5]),yUp+(yMid-yUp)/5*(5-list[5])) // scenery, point6
//            val rightDownPoint = Point(xRight-(xRight-xMid)/5*(5-list[4]),yDown-(yDown-yMid)/5*(5-list[4])) // vibe, point5
            val path = Path()
            path.moveTo(xMid,yMid/5*(5-rating.coverage)) // upPoint
            path.lineTo(xLeft+(xMid-xLeft)/5*(5-rating.tranquility),yUp+(yMid-yUp)/5*(5-rating.tranquility)) //leftUpPoint
            path.lineTo(xLeft+(xMid-xLeft)/5*(5-rating.snack),yDown-(yDown-yMid)/5*(5-rating.snack)) // leftDownPoint
            path.lineTo(xMid,yMid/5*(5+rating.rest)) //downPoint
            path.lineTo(xRight-(xRight-xMid)/5*(5-rating.vibe),yDown-(yDown-yMid)/5*(5-rating.vibe)) // rightDownPoint
            path.lineTo(xRight-(xRight-xMid)/5*(5-rating.scenery),yUp+(yMid-yUp)/5*(5-rating.scenery)) //rightUpPoint
            path.close()
            canvas.drawPath(path,paint)

//            paint.color = getColor(R.color.secondaryDarkColor)
//
//            paint.style = Paint.Style.STROKE
//            paint.strokeWidth = WalkableApp.instance.resources
//                .getDimensionPixelSize(R.dimen.hexagon_stroke).toFloat()
//
//            canvas.drawPath(path,paint)
        }
    })

}





//mock data area
const val userId = "10043"


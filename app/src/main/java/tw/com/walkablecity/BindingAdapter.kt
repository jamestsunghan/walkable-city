package tw.com.walkablecity


import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toPointF
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.lessThenTenPadStart
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.loadroute.route.RouteItem
import tw.com.walkablecity.loadroute.route.RouteItemAdapter
import java.text.SimpleDateFormat
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
        }
    }

}

@BindingAdapter("status", "route", "time")
fun walkerTimer(textView: TextView, status: WalkerStatus, route: Route?, time: Long){
    val minutes = time / 60
    val seconds = time % 60
    val timeText = StringBuilder().append(lessThenTenPadStart(minutes))
        .append(":").append(lessThenTenPadStart(seconds)).toString()
    when(status){
        WalkerStatus.PREPARE -> {
            if(route == null){
                textView.text = getString(R.string.zero_time)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
            }else{
                textView.text = String.format(getString(R.string.approximate_time), route.minutes)
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
fun walkerDistance(textView: TextView, status: WalkerStatus, route: Route?, distance: Float){
    when(status){
        WalkerStatus.PREPARE -> {
            if(route == null){
                textView.text = getString(R.string.zero_distance)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
            }else{
                textView.text = String.format(getString(R.string.approximate_length), route.length)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24F)
            }
        }
        else -> {
            textView.text = distance.toString()
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
        }

    }
}

@BindingAdapter("hexagon")
fun bgHexagonByColorCode(view: View, colorId: Int) {

    view.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {


            paint.color = WalkableApp.instance.resources.getColor(colorId, WalkableApp.instance.theme)

            paint.style = Paint.Style.FILL
            val upPoint = Point((this.width/2).toInt(),0)
            val downPoint = Point((this.width/2).toInt(),this.height.toInt())
            val midPoint = Point((this.width/2).toInt(),(this.height/2).toInt())
            val leftUpPoint = Point((this.width/2-(sqrt(3.0) *this.height/4)).toInt(),(this.height/4).toInt())
            val leftDownPoint = Point((this.width/2-(sqrt(3.0) *this.height/4)).toInt(),(this.height*3/4).toInt())
            val rightUpPoint = Point((this.width/2+(sqrt(3.0) *this.height/4)).toInt(),(this.height/4).toInt())
            val rightDownPoint = Point((this.width/2+(sqrt(3.0) *this.height/4)).toInt(),(this.height*3/4).toInt())
            val path = Path()
            path.moveTo(upPoint.x.toFloat(),upPoint.y.toFloat())
            path.lineTo(leftUpPoint.x.toFloat(),leftUpPoint.y.toFloat())
            path.lineTo(leftDownPoint.x.toFloat(),leftDownPoint.y.toFloat())
            path.lineTo(downPoint.x.toFloat(),downPoint.y.toFloat())
            path.lineTo(rightDownPoint.x.toFloat(),rightDownPoint.y.toFloat())
            path.lineTo(rightUpPoint.x.toFloat(),rightUpPoint.y.toFloat())
            path.close()
            canvas.drawPath(path,paint)

//            paint.style = Paint.Style.FILL
//            canvas.drawRect(0f, 0f, this.width, this.height, paint)
//
//            paint.color = Color.BLACK
//            paint.style = Paint.Style.STROKE
//            paint.strokeWidth = StylishApplication.instance.resources
//                .getDimensionPixelSize(R.dimen.edge_detail_color).toFloat()
//            canvas.drawRect(0f, 0f, this.width, this.height, paint)
        }
    })

}

@BindingAdapter("power")
fun hexagonByRating(view: View, list: List<Int>) {

    view.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {


            paint.color = WalkableApp.instance.resources.getColor(R.color.primaryDarkColor, WalkableApp.instance.theme)

            paint.style = Paint.Style.FILL

            val xLeft = (this.width/2-(sqrt(3.0) *this.height/4)).toInt()
            val xRight = (this.width/2+(sqrt(3.0) *this.height/4)).toInt()
            val yUp = (this.height/4).toInt()
            val yDown = (this.height*3/4).toInt()
            val xMid = (this.width/2).toInt()
            val yMid = (this.height/2).toInt()
            val upPoint = Point(xMid,(yMid/5*(5-list[0])))
            val downPoint = Point(xMid,(yMid/5*(5+list[3])))
            val midPoint = Point(xMid,yMid)
            val leftUpPoint = Point(xLeft+(xMid-xLeft)/5*(5-list[1]),yUp+(yMid-yUp)/5*(5-list[1]))
            val leftDownPoint = Point(xLeft+(xMid-xLeft)/5*(5-list[2]),yDown-(yDown-yMid)/5*(5-list[2]))
            val rightUpPoint = Point(xRight-(xRight-xMid)/5*(5-list[5]),yUp+(yMid-yUp)/5*(5-list[5]))
            val rightDownPoint = Point(xRight-(xRight-xMid)/5*(5-list[4]),yDown-(yDown-yMid)/5*(5-list[4]))
            val path = Path()
            path.moveTo(upPoint.x.toFloat(),upPoint.y.toFloat())
            path.lineTo(leftUpPoint.x.toFloat(),leftUpPoint.y.toFloat())
            path.lineTo(leftDownPoint.x.toFloat(),leftDownPoint.y.toFloat())
            path.lineTo(downPoint.x.toFloat(),downPoint.y.toFloat())
            path.lineTo(rightDownPoint.x.toFloat(),rightDownPoint.y.toFloat())
            path.lineTo(rightUpPoint.x.toFloat(),rightUpPoint.y.toFloat())
            path.close()
            canvas.drawPath(path,paint)

//            paint.style = Paint.Style.FILL
//            canvas.drawRect(0f, 0f, this.width, this.height, paint)
//
//            paint.color = Color.BLACK
//            paint.style = Paint.Style.STROKE
//            paint.strokeWidth = StylishApplication.instance.resources
//                .getDimensionPixelSize(R.dimen.edge_detail_color).toFloat()
//            canvas.drawRect(0f, 0f, this.width, this.height, paint)
        }
    })

}





//mock data area

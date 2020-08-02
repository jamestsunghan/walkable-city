package tw.com.walkablecity


import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toPointF
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.FragmentActivity
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
import tw.com.walkablecity.Util.setDp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.detail.CommentAdapter
import tw.com.walkablecity.detail.DetailCircleAdapter
import tw.com.walkablecity.detail.ImageUrlAdapter
import tw.com.walkablecity.event.item.EventItemAdapter
import tw.com.walkablecity.eventdetail.FrequencyAdapter
import tw.com.walkablecity.eventdetail.FrequencyFriendAdapter
import tw.com.walkablecity.eventdetail.MemberAdapter
import tw.com.walkablecity.ext.saveToInternalStorage
import tw.com.walkablecity.ext.shareCacheDirBitmap
import tw.com.walkablecity.ext.toMemberItem
import tw.com.walkablecity.ext.toWalkerItem
import tw.com.walkablecity.favorite.FavoriteAdapter
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.host.add2event.AddFriend2EventAdapter
import tw.com.walkablecity.host.add2event.AddListAdapter
import tw.com.walkablecity.loadroute.route.RouteItem
import tw.com.walkablecity.loadroute.route.RouteItemAdapter
import tw.com.walkablecity.profile.bestwalker.BestWalkersAdapter
import tw.com.walkablecity.ranking.RankingAdapter
import tw.com.walkablecity.rating.RatingType
import tw.com.walkablecity.rating.item.RatingItemPhotoAdapter
import java.io.ByteArrayOutputStream
import java.io.File
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

@BindingAdapter("counting")
fun bindByCounts(view: RecyclerView, count: Int?){
    count?.let{item->
        Logger.d("JJ_snap count $item")
        view.adapter.apply {
            when(this){
                is DetailCircleAdapter -> submitCount(item)
            }
        }
    }
}

@BindingAdapter("circleStatus")
fun bindDetailCircleStatus(imgView: ImageView, isSelected: Boolean = false){
    imgView.background = ShapeDrawable(object : Shape(){
        override fun draw(canvas: Canvas, paint: Paint) {
            paint.color = getColor(R.color.red_heart_c73e3a)
            paint.isAntiAlias = true

            if(isSelected){
                paint.style = Paint.Style.FILL
            }else{
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = setDp(1f)
            }

            canvas.drawCircle(width/2, height/2, setDp(3f),paint)
        }
    })
}

@BindingAdapter("addDecoration")
fun bindDecoration(recyclerView: RecyclerView, decoration: RecyclerView.ItemDecoration?) {
    decoration?.let { recyclerView.addItemDecoration(it) }
}


@BindingAdapter("walker")
fun bindBestWalkers(view: RecyclerView, list: List<User>?){
    list?.let{item->
        view.adapter.apply {
            when(this){
                is BestWalkersAdapter -> submitList(item.toWalkerItem())
            }
        }
    }
}

@BindingAdapter("photopts")
fun bindPhotoPoints(view: RecyclerView, list: List<PhotoPoint>?){
    list?.let{item->
        view.adapter.apply {
            when(this){
                is RatingItemPhotoAdapter -> submitList(item)
            }
        }
    }
}

@BindingAdapter("routeImage")
fun bindRouteImages(view: RecyclerView, list: List<String>?){
    list?.let{
        view.adapter.apply {
            when(this){
                is ImageUrlAdapter -> submitList(it)

            }
        }
    }
}

@BindingAdapter("friendly")
fun bindFriends(view: RecyclerView, list: List<Friend>?){
    list?.let{item->
        view.adapter.apply {
            when(this){
                is AddFriend2EventAdapter -> submitList(item)
                is AddListAdapter -> submitList(item)
                is FrequencyFriendAdapter -> submitList(item)
            }
        }
    }
}

@BindingAdapter("friendwrapper")
fun bindFriendLists(view: RecyclerView, array: Array<Friend>?){
    val list = array?.toList()
    list?.let{item->
        view.adapter.apply {
            when(this){
                is FrequencyFriendAdapter -> submitList(item)
            }
        }
    }
}

@BindingAdapter("fqlist")
fun bindListOfList(view: RecyclerView, list: List<FriendListWrapper>?){
    list?.let{item->
        view.adapter.apply {
            when(this){
                is FrequencyAdapter -> submitList(item)
            }
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

@BindingAdapter("friend")
fun bindFriend(view: RecyclerView, list: List<Friend>?){
    list?.let{
        view.adapter.apply {
            when(this){
                is MemberAdapter -> submitList(it.toMemberItem())

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


@BindingAdapter("progress", "wid", "targeter")
fun bindWidthWithFloat(view: TextView, input: Float?, width: Int, target: EventTarget){
    input?.let{
        if(width != 0){

            val layoutParams = view.layoutParams
            layoutParams.width = if(target.frequencyType == null){
                input.times(width).div(target.distance ?: requireNotNull(target.hour)*3600).toInt()
            }else{
                input.times(width).div(target.distance ?: requireNotNull(target.hour)).toInt()
            }

            view.layoutParams = layoutParams
        }
    }
}

@BindingAdapter("progress", "targeting")
fun bindTextWithFloat(view: TextView, input: Float?, target: EventTarget){
    input?.let{
        val percentage = if(target.frequencyType == null){
            input.div(target.distance ?: requireNotNull(target.hour)*3600).times(100)
        }else{
            input.div(target.distance ?: requireNotNull(target.hour)).times(100)
        }

        view.text = String.format(getString(R.string.accomplish_rate), percentage)
    }

}

@BindingAdapter("detailType", "goal")
fun bindTargetWithType(textView: TextView, type: EventType, goal:EventTarget){
    val end = if(goal.distance == null) getString(R.string.walk_accumulate_hours) else getString(R.string.walk_accumulate_km)
    textView.text = when(type){
        EventType.FREQUENCY      -> StringBuilder().append(getString(R.string.event_goal)).append(goal.frequencyType?.text).append(String.format(end,goal.distance ?: goal.hour)).toString()
        EventType.DISTANCE_GROUP -> StringBuilder().append(getString(R.string.event_group_goal)).append(String.format(getString(R.string.walk_accumulate_km), goal.distance)).toString()
        EventType.DISTANCE_RACE  -> StringBuilder().append(getString(R.string.event_race_goal)).append(String.format(getString(R.string.walk_accumulate_km), goal.distance)).toString()
        EventType.HOUR_GROUP     -> StringBuilder().append(getString(R.string.event_group_goal)).append(String.format(getString(R.string.walk_accumulate_hours), goal.hour)).toString()
        EventType.HOUR_RACE      -> StringBuilder().append(getString(R.string.event_race_goal)).append(String.format(getString(R.string.walk_accumulate_hours), goal.hour)).toString()
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

@BindingAdapter("mapRouteTime")
fun mapRouteTimeDisplay(textView: TextView, mapRoute: GoogleRoute? ){
    mapRoute?.let{
        var durationTotal = 0F
        for(item in mapRoute.legs){
            durationTotal += item.duration?.value?.toFloat() ?: 0F
        }
        textView.text = String.format(getString(R.string.approximate_time), durationTotal / 60)
    }
}

@BindingAdapter("mapRouteDistance")
fun mapRouteDistanceDisplay(textView: TextView, mapRoute: GoogleRoute? ){
    mapRoute?.let{
        var distanceTotal = 0F
        for(item in mapRoute.legs){
            distanceTotal += item.distance?.value?.toFloat() ?: 0F
        }
        textView.text = String.format(getString(R.string.approximate_length), distanceTotal / 1000)
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

        Logger.d("JJ_ref reference $reference")

        Glide.with(imageView.context)
            .load(reference).apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
            )
            .into(imageView)
    }

}

@BindingAdapter("glide")
fun glidingImage(imageView: ImageView, url: String?){
    url?.let{
        val uri = it.toUri().buildUpon().build()

        Glide.with(imageView.context)
            .load(uri).apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
            )
            .into(imageView)
    }

}

@BindingAdapter("reference", "type")
fun glidingImage(imageView: ImageView, ref: String?, type: RatingType){
    ref?.let{reference->
        val glider = when(type){
        RatingType.WALK ->FileProvider.getUriForFile(WalkableApp.instance, WalkableApp.instance.packageName + ".provider"
            , File(reference))
        RatingType.ROUTE ->  Firebase.storage.reference.child(reference)
    }

        Glide.with(imageView.context)
            .load(glider).apply(
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

@BindingAdapter("app:srcCompat")
fun setDrawableToImageView(imageView: ImageView, drawable: Drawable){
    imageView.setImageDrawable(drawable)

//    imageView.setOnClickListener {view->
//        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        drawable.setBounds(0,0,canvas.width, canvas.height)
//        drawable.draw(canvas)
//
//        bitmap.saveToInternalStorage(WalkableApp.instance)
//
//        activity.shareCacheDirBitmap()
//
//    }

}

@BindingAdapter("app:srcCompat", "send", "shareable")
fun setDrawableAndSendImageView(imageView: ImageView, drawable: Drawable, activity: FragmentActivity, shareable: Boolean){
    imageView.setImageDrawable(drawable)

    imageView.setOnClickListener {view->
        if(shareable){
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val bitmapScale = Bitmap.createScaledBitmap(bitmap, 120, 120, false)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0,0,canvas.width, canvas.height)
            drawable.draw(canvas)

            bitmapScale.saveToInternalStorage(WalkableApp.instance)

            activity.shareCacheDirBitmap()
        }
    }

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

@BindingAdapter("dataTotal")
fun sweepDataTotal(view: TextView, list: List<Float>?){

    list?.let{
        view.text = String.format(getString(R.string.accomplish_rate), it.sum().times(100))
    }
}

@BindingAdapter("sweepWithData", "type")
fun sweepWithData(view: CircleView, list: List<Float>?, type: EventType){

//    val paint = Paint()
//    val w = view.width
//    val h = view.height
//    var startAngle = 0f

//    val pl = view.paddingLeft
//    val pr = view.paddingRight
//    val pt = view.paddingTop
//    val pb = view.paddingBottom
//
//    val usableWidth = w - (pl + pr)
//    val usableHeight = h - (pt + pb)
//
//    val radius = usableWidth.coerceAtMost(usableHeight) / 2
//    val cx = pl + usableWidth / 2
//    val cy = pt + usableHeight / 2
//    val rateColor = listOf<Int>(
//        getColor(R.color.secondaryLightColor), getColor(R.color.secondaryColor), getColor(R.color.secondaryDarkColor)
//    )
//    paint.style = Paint.Style.STROKE
//    paint.strokeWidth = view.getStrokeWidth()
//    view.foreground = ShapeDrawable(object: Shape(){
//        override fun draw(canvas: Canvas?, paint: Paint?) {
//
//        }
//    })

    list?.let{
        view.setRateList(list)
        view.setRateListColor(type.colorList)
        view.invalidate()
//        if(list.isNullOrEmpty()) canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint)
//        else {
//            for((listPosition, item) in list.withIndex()){
//                paint.color = rateColor[listPosition % 3]
//                canvas.drawArc(cx.toFloat()- radius.toFloat(), cy.toFloat()-radius.toFloat()
//                    , cx.toFloat() + radius.toFloat(), cy.toFloat()+radius.toFloat()
//                    , startAngle, item.times(360), false, paint)
//                startAngle += item.times(360)
//            }
//        }

    }
}





//mock data area



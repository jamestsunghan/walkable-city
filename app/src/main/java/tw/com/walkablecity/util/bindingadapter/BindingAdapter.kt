package tw.com.walkablecity.util.bindingadapter


import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.slider.Slider
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tw.com.walkablecity.R
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.util.Util.lessThenTenPadStart
import tw.com.walkablecity.data.*
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.rating.RatingType
import tw.com.walkablecity.util.CircleView
import tw.com.walkablecity.util.Logger
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Deal with different eventType and eventTarget.
 * Show member's accomplish percentage base on event type and target.
 */


@BindingAdapter("progress", "targeting")
fun bindTextWithFloat(view: TextView, input: Float?, target: EventTarget) {
    input?.let {
        val percentage = if (target.frequencyType == null) {
            input.div(target.distance ?: requireNotNull(target.hour) * 3600).times(100)
        } else {
            input.div(target.distance ?: requireNotNull(target.hour)).times(100)
        }

        view.text = String.format(getString(R.string.accomplish_rate), percentage)
    }
}

/**
 * Deal with different eventType and eventTarget.
 * Show event's target base on event type.
 */

@BindingAdapter("detailType", "goal")
fun bindTargetWithType(textView: TextView, type: EventType, goal: EventTarget) {
    textView.text = type.buildTargetString(goal)
}

/**
 * Deal with different eventType and eventTarget.
 * Show event's challenge title base on event type.
 */

@BindingAdapter("target")
fun bindTextWithEventType(textView: TextView, type: EventType?) {

    textView.text = type?.eventCreateTitle ?: getString(R.string.not_here)
    textView.visibility = if (type == null) View.GONE else View.VISIBLE
}

/**
 * Deal with different eventType and eventTarget.
 * Show event's challenge title base on event type in spinner.
 */

@BindingAdapter("typePosition")
fun bindTextWithEventTypePosition(textView: TextView, position: Int?) {

    val distancePos = listOf(1,3,4)
    val hourPos = listOf(2,5,6)

    textView.text = when (position) {
        in distancePos -> getString(R.string.create_event_frequency_distance)
        in hourPos     -> getString(R.string.create_event_frequency_hour)
        else           -> getString(R.string.not_here)
    }
    textView.visibility = if (position == null || position == 0 || position > 6) View.GONE
    else View.VISIBLE

}

/**
 * Deal with WalkerStatus and walk recording.
 * Show timer or route time base on loaded route or walking status.
 */

@BindingAdapter("status", "route", "time")
fun walkerTimer(textView: TextView, status: WalkerStatus, mapRoute: GoogleRoute?, time: Long) {
    val minutes = time / 60
    val seconds = time % 60
    val timeText = StringBuilder().append(lessThenTenPadStart(minutes))
        .append(":").append(lessThenTenPadStart(seconds)).toString()
    when (status) {
        WalkerStatus.PREPARE -> {

            if (mapRoute == null) {
                textView.text = getString(R.string.zero_time)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32F)
            } else {
                val durationTotal = mapRoute.minuteSum()

                textView.text = String.format(getString(R.string.approximate_time), durationTotal / 60)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
            }
        }
        else -> {
            textView.text = timeText
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32F)
        }
    }
}

/**
 * Deal with WalkerStatus and walk recording.
 * Show distance or route distance base on loaded route or walking status.
 */

@BindingAdapter("status", "route", "distance")
fun walkerDistance(
    textView: TextView,
    status: WalkerStatus,
    mapRoute: GoogleRoute?,
    distance: Float
) {
    when (status) {
        WalkerStatus.PREPARE -> {
            if (mapRoute == null) {
                textView.text = getString(R.string.zero_distance)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32F)
            } else {
                val distanceTotal = mapRoute.distanceSum()

                textView.text = String.format(getString(R.string.approximate_length), distanceTotal / 1000)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
            }
        }
        else -> {
            textView.text = String.format(getString(R.string.recording_length), distance)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32F)
        }

    }
}

/**
 * Deal with MapRoute.
 * Show approximate route time base on loaded route.
 */

@BindingAdapter("mapRouteTime")
fun mapRouteTimeDisplay(textView: TextView, mapRoute: GoogleRoute?) {
    mapRoute?.let {
        val durationTotal = mapRoute.minuteSum()
        textView.text = String.format(getString(R.string.approximate_time), durationTotal / 60)
    }
}

/**
 * Deal with MapRoute.
 * Show approximate route time base on loaded route.
 */

@BindingAdapter("mapRouteDistance")
fun mapRouteDistanceDisplay(textView: TextView, mapRoute: GoogleRoute?) {
    mapRoute?.let {
        val distanceTotal = mapRoute.distanceSum()
        textView.text = String.format(getString(R.string.approximate_length), distanceTotal / 1000)
    }
}

/**
 * Two-way binding for slider.
 */


@BindingAdapter("value")
fun convertInt2Float(slider: Slider, input: Int) {
    slider.value = input.toFloat()
}

@InverseBindingAdapter(attribute = "value")
fun convertFloat2Int(slider: Slider): Int {
    return slider.value.toInt()
}

@BindingAdapter("valueAttrChanged")
fun setSliderListeners(slider: Slider, attrChange: InverseBindingListener) {
    slider.addOnChangeListener { _, _, _ ->
        attrChange.onChange()
    }
}

@BindingAdapter("switch")
fun switchDrawable(imageView: ImageView, added: Boolean) {
    when (added) {
        true -> imageView.setImageResource(R.drawable.map_fav_24_added)
        false -> imageView.setImageResource(R.drawable.map_fav_24)
    }
}

/**
 * Glide with firebase storage url.
 */

@BindingAdapter("imageUrl")
fun glideImage(imageView: ImageView, url: String?) {
    url?.let {
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

/**
 * Glide with normal url.
 */

@BindingAdapter("glide")
fun glidingImage(imageView: ImageView, url: String?) {
    url?.let {
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

/**
 * Glide with local file uri or firebase uri.
 */

@BindingAdapter("reference", "type")
fun glidingImage(imageView: ImageView, ref: String?, type: RatingType) {
    ref?.let { reference ->
        val glider = when (type) {
            RatingType.WALK -> FileProvider.getUriForFile(
                WalkableApp.instance, WalkableApp.instance.packageName + ".provider"
                , File(reference)
            )
            RatingType.ROUTE -> Firebase.storage.reference.child(reference)
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

/**
 * Display timestamp in date string format.
 */

@BindingAdapter("date")
fun timeStampToDate(textView: TextView, time: Timestamp) {
    textView.text =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN).format(time.seconds.times(1000))
}

/**
 * Deal with accomplish of each member.
 * Display members' accomplishment in total by text.
 */

@BindingAdapter("dataTotal")
fun sweepDataTotal(view: TextView, list: List<Float>?) {

    list?.let {
        view.text = String.format(getString(R.string.accomplish_rate), it.sum().times(100))
    }
}

/**
 * Deal with accomplish of each member.
 * Display each member's accomplishment by circle view.
 */

@BindingAdapter("sweepWithData", "type")
fun sweepWithData(view: CircleView, list: List<Float>?, type: EventType) {

    list?.let {
        view.setRateList(list)
        view.setRateListColor(type.colorList)
        view.invalidate()

    }
}



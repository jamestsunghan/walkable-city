package tw.com.walkablecity.util.bindingadapter

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentActivity
import tw.com.walkablecity.R
import tw.com.walkablecity.WalkableApp
import tw.com.walkablecity.data.RouteRating
import tw.com.walkablecity.ext.saveToInternalStorage
import tw.com.walkablecity.ext.shareCacheDirBitmap
import tw.com.walkablecity.util.Util.getColor
import tw.com.walkablecity.util.Util.setDp
import kotlin.math.sqrt


/**
 * Deal with picture gallery positions.
 * Draw circles solid or by stroke base on position selected or not.
 */

@BindingAdapter("circleStatus")
fun bindDetailCircleStatus(imgView: ImageView, isSelected: Boolean = false) {
    imgView.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {
            paint.color = getColor(R.color.red_heart_c73e3a)
            paint.isAntiAlias = true

            if (isSelected) {
                paint.style = Paint.Style.FILL
            } else {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = setDp(1f)
            }

            canvas.drawCircle(width / 2, height / 2, setDp(3f), paint)
        }
    })
}

/**
 * Deal with route ratings.
 * Draw background hexagon by color id.
 */

@BindingAdapter("hexagon")
fun bgHexagonByColorCode(view: View, colorId: Int) {

    view.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {

            paint.color = getColor(colorId)

            paint.style = Paint.Style.FILL

            val xLeft  = (this.width / 2 - (sqrt(3.0) * this.height / 4)).toFloat()
            val xRight = (this.width / 2 + (sqrt(3.0) * this.height / 4)).toFloat()
            val xMid   = (this.width / 2)
            val yUp    = (this.height / 4)
            val yDown  = (this.height * 3 / 4)
            val height =  this.height

            val path = Path()

            path.apply{
                moveTo(xMid, 0f) // upPoint
                lineTo(xLeft , yUp) //leftUpPoint
                lineTo(xLeft, yDown) // leftDownPoint
                lineTo(xMid, height) //downPoint
                lineTo(xRight , yDown) // rightDownPoint
                lineTo(xRight, yUp) //rightUpPoint
                close()
            }

            canvas.drawPath(path, paint)

            paint.style = Paint.Style.STROKE

            paint.color = getColor(R.color.primaryDarkColor)
            paint.strokeWidth = WalkableApp.instance.resources
                .getDimensionPixelSize(R.dimen.hexagon_stroke).toFloat()

            canvas.drawLine(xMid, 0f, xMid, height, paint)
            canvas.drawLine(xLeft, yUp, xRight, yDown, paint)
            canvas.drawLine(xLeft, yDown, xRight, yUp, paint)


        }
    })

}

/**
 * Deal with route ratings.
 * Drawable hexagon by route rating.
 */

@BindingAdapter("power")
fun hexagonByRating(view: View, rating: RouteRating) {

    view.background = ShapeDrawable(object : Shape() {
        override fun draw(canvas: Canvas, paint: Paint) {


            paint.color = getColor(R.color.hexagon_fill)

            paint.style = Paint.Style.FILL

            val xLeft = (this.width / 2 - (sqrt(3.0) * this.height / 4)).toFloat()
            val xRight = (this.width / 2 + (sqrt(3.0) * this.height / 4)).toFloat()
            val yUp = (this.height / 4)
            val yDown = (this.height * 3 / 4)
            val xMid = (this.width / 2)
            val yMid = (this.height / 2)

            val path = Path()

            path.apply{
                moveTo(xMid, yMid / 5 * (5 - rating.coverage)) // upPoint
                lineTo(
                    xMid + (xLeft - xMid) / 5 * (rating.tranquility),
                    yMid + (yUp - yMid)   / 5 * (rating.tranquility)
                ) //leftUpPoint
                lineTo(
                    xMid + (xLeft - xMid) / 5 * (rating.snack),
                    yMid + (yDown - yMid) / 5 * (rating.snack)
                ) // leftDownPoint
                lineTo(xMid, yMid / 5 * (5 + rating.rest)) //downPoint
                lineTo(
                    xMid + (xRight - xMid) / 5 * (rating.vibe),
                    yMid + (yDown - yMid) / 5 * (rating.vibe)
                ) // rightDownPoint
                lineTo(
                    xMid + (xRight - xMid) / 5 * (rating.scenery),
                    yMid + (yUp - yMid) / 5 * (rating.scenery)
                ) //rightUpPoint
                close()
            }
            canvas.drawPath(path, paint)
        }
    })

}

/**
 * Deal with shareable images.
 * Transform drawable to bitmap, store in cache and share.
 */

@BindingAdapter("app:srcCompat", "send", "shareable")
fun setDrawableAndSendImageView(
    imageView: ImageView,
    drawable: Drawable,
    activity: FragmentActivity,
    shareable: Boolean
) {
    imageView.setImageDrawable(drawable)

    imageView.setOnClickListener {
        if (shareable) {

            drawable.toBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight).apply{
                saveToInternalStorage(WalkableApp.instance, activity)
            }
        }
    }
}
package tw.com.walkablecity.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getColor
import tw.com.walkablecity.WalkableApp


class CircleView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        private const val DEFAULT_CIRCLE_COLOR = Color.RED
    }

    private val defaultStrokeWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 5f,
        WalkableApp.instance.resources.displayMetrics
    )

    private val paint = Paint()

    private var circleColor = DEFAULT_CIRCLE_COLOR

    private var strokewidth = defaultStrokeWidth

    private var rate = listOf<Float>()

    private var rateColor = listOf(
        getColor(R.color.secondaryLightColor),
        getColor(R.color.secondaryColor),
        getColor(R.color.secondaryDarkColor)
    )

    private var startAngle = -90f

    init {
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width
        val h = height

        val pl = paddingLeft
        val pr = paddingRight
        val pt = paddingTop
        val pb = paddingBottom

        val usableWidth = w - (pl + pr)
        val usableHeight = h - (pt + pb)

        val radius = usableWidth.coerceAtMost(usableHeight) / 2
        val cx = pl + usableWidth / 2
        val cy = pt + usableHeight / 2

        paint.color = circleColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokewidth
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint)

        if (rate.isNotEmpty()) {

            startAngle = -90f

            for ((listPosition, item) in rate.withIndex()) {
                paint.color = rateColor[listPosition % 3]
                Logger.d("JJ_circle rate item $item")
                canvas.drawArc(
                    cx.toFloat() - radius.toFloat(), cy.toFloat() - radius.toFloat()
                    , cx.toFloat() + radius.toFloat(), cy.toFloat() + radius.toFloat()
                    , startAngle, item.times(360f), false, paint
                )
                startAngle += item.times(360f)
            }

        }
    }


    fun setCircleColor(circleColor: Int) {
        this.circleColor = circleColor
        invalidate()
    }

    fun getCircleColor(): Int {
        return this.circleColor
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokewidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, strokeWidth,
            WalkableApp.instance.resources.displayMetrics
        )
        invalidate()
    }

    fun getStrokeWidth(): Float {
        return this.strokewidth

    }

    fun setRateList(list: List<Float>) {
        this.rate = list
    }

    fun setRateListColor(list: List<Int>) {
        this.rateColor = list
    }
}
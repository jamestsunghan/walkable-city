package tw.com.walkablecity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import tw.com.walkablecity.Util.getColor


class CircleView(context: Context, attrs: AttributeSet): View(context, attrs) {

    companion object {
        private const val DEFAULT_CIRCLE_COLOR = Color.RED
    }
    private val DEFAULT_STROKE_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f,WalkableApp.instance.resources.displayMetrics)

    private val paint = Paint()
    private var circleColor = DEFAULT_CIRCLE_COLOR
    private var strokewidth = DEFAULT_STROKE_WIDTH
    private var rate = listOf<Float>()
    private var rateColor = listOf<Int>(
        getColor(R.color.secondaryLightColor), getColor(R.color.secondaryColor), getColor(R.color.secondaryDarkColor)
    )
    private var startAngle = -90f


    init {
        paint.isAntiAlias = true
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
    }

    override fun setForeground(foreground: Drawable?) {

        super.setForeground(foreground)
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

        if(rate.isNullOrEmpty()){
//            canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint)
        }
        else {
            startAngle = -90f
            for((listPosition, item) in rate.withIndex()){
                paint.color = rateColor[listPosition % 3]
                Log.d("JJ_circle","rate item $item")
                canvas.drawArc(cx.toFloat()- radius.toFloat(), cy.toFloat()-radius.toFloat()
                    , cx.toFloat() + radius.toFloat(), cy.toFloat() + radius.toFloat()
                    , startAngle, item.times(360f), false, paint )
                startAngle += item.times(360f)
            }
        }

    }


    fun setCircleColor(circleColor: Int){
        this.circleColor = circleColor
        invalidate()
    }

    fun getCircleColor(): Int{
        return this.circleColor
    }

    fun setStrokeWidth(strokeWidth: Float){
        this.strokewidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth,WalkableApp.instance.resources.displayMetrics)
        invalidate()
    }

    fun getStrokeWidth(): Float{
        return this.strokewidth

    }

    fun setRateList(list: List<Float>){
        this.rate = list
    }

    fun setRateListColor(list: List<Int>){
        this.rateColor = list
    }
}
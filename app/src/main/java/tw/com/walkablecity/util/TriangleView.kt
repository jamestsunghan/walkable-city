package tw.com.walkablecity.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getColor

class TriangleView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val defaultColor = getColor(R.color.primaryDarkColor)

    private val paint = Paint()
    private val path = Path()
    private var triangleColor = defaultColor

    init {
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        paint.color = triangleColor
        paint.style = Paint.Style.FILL
        path.moveTo(0f, 0f)
        path.lineTo(w, 0f)
        path.lineTo(w.div(2), h)
        path.close()

        canvas.drawPath(path, paint)

    }
}
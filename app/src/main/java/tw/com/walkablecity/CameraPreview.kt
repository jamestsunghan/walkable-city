package tw.com.walkablecity

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.TextureView
import java.io.File
import kotlin.math.roundToInt

class CameraPreview(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : SurfaceView(context, attrs, defStyle) {

    private var aspectRatio = 0f

    fun setAspectRatio(width: Int, height: Int){
        require(width > 0 && height > 0)
        aspectRatio = width.toFloat() / height.toFloat()
        holder.setFixedSize(width, height)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if(aspectRatio == 0f){
            setMeasuredDimension(width, height)
        }else{

            // Performs center-crop transformation of the camera frames
            val newWidth: Int
            val newHeight: Int
            val actualRatio = if (width > height) aspectRatio else 1f / aspectRatio
            if (width < height * actualRatio) {
                newHeight = height
                newWidth = (height * actualRatio).roundToInt()
            } else {
                newWidth = width
                newHeight = (width / actualRatio).roundToInt()
            }

            Logger.d("JJ_camera Measured dimensions set: $newWidth x $newHeight")
            setMeasuredDimension(newWidth, newHeight)
        }
    }

    fun onResume(activity: Activity){

    }

    fun onPause(activity: Activity){

    }

    fun setOutputDir(file: File){

    }

}
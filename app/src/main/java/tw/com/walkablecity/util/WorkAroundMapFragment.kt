package tw.com.walkablecity.util


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.maps.SupportMapFragment
import tw.com.walkablecity.R

class WorkaroundMapFragment : SupportMapFragment() {
    private var onTouchListener: OnTouchListener? = null
    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstance: Bundle?
    ): View? {
        val layout = super.onCreateView(layoutInflater, viewGroup, savedInstance)
        val frameLayout = TouchableWrapper(requireActivity())
        frameLayout.setBackgroundColor(
            resources.getColor(R.color.real_transparent, requireContext().theme)
        )
        (layout as ViewGroup?)!!.addView(
            frameLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        return layout
    }

    fun setListener(listener: OnTouchListener?) {
        onTouchListener = listener
    }

    interface OnTouchListener {
        fun onTouch()
    }

    inner class TouchableWrapper(context: Context) :
        FrameLayout(context) {
        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> onTouchListener?.onTouch()
                MotionEvent.ACTION_UP -> onTouchListener?.onTouch()
            }
            return super.dispatchTouchEvent(event)
        }
    }
}
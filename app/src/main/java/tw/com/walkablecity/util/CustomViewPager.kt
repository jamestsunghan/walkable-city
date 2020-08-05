package tw.com.walkablecity.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment

class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    override fun canScroll(v: View?, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is MapView || v is SupportMapFragment || v is RecyclerView) {
            return true
        }

        return super.canScroll(v, checkV, dx, x, y)
    }
}
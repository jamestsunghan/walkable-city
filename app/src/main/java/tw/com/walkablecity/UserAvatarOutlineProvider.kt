package tw.com.walkablecity

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class UserAvatarOutlineProvider: ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        view.clipToOutline = true
        val radius = WalkableApp.instance.resources.getDimensionPixelSize(R.dimen.radius_add_friend)
        outline.setOval(0,0,radius,radius)
    }
}
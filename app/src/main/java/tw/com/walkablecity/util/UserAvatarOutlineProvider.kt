package tw.com.walkablecity.util

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import tw.com.walkablecity.R
import tw.com.walkablecity.WalkableApp

class UserAvatarOutlineProvider: ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        view.clipToOutline = true
        val radius = WalkableApp.instance.resources.getDimensionPixelSize(
            R.dimen.radius_add_friend
        )
        outline.setOval(0,0,radius,radius)
    }
}
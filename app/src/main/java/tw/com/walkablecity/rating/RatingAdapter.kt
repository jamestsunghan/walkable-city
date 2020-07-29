package tw.com.walkablecity.rating

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import tw.com.walkablecity.data.PhotoPoint
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.rating.item.RatingItemFragment


class RatingAdapter(fragmentManager: FragmentManager
                    , val route: Route?, val walk: Walk
                    , private val willComment: Boolean, private val photoWalkPoints: List<PhotoPoint>?): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return RatingItemFragment(RatingType.values()[position].apply{
            when(this){
                RatingType.WALK -> this.willComment = this@RatingAdapter.willComment
                else->{}
            }
        }, route, walk, photoWalkPoints)
    }

    override fun getCount(): Int = RatingType.values().size

    override fun getPageTitle(position: Int): CharSequence? {
        return RatingType.values()[position].title
    }

}
package tw.com.walkablecity.rating

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import tw.com.walkablecity.rating.item.RatingItemFragment


class RatingAdapter(fragmentManager: FragmentManager, val viewModel: RatingViewModel): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return RatingItemFragment(RatingType.values()[position], viewModel.route, viewModel.walk)
    }

    override fun getCount(): Int = RatingType.values().size

    override fun getPageTitle(position: Int): CharSequence? {
        return RatingType.values()[position].title
    }

}
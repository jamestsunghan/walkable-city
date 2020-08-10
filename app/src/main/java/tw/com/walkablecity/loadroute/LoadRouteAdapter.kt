package tw.com.walkablecity.loadroute

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import tw.com.walkablecity.loadroute.route.RouteItemFragment

class LoadRouteAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return RouteItemFragment(LoadRouteType.values()[position])
    }

    override fun getCount() = LoadRouteType.values().size

    override fun getPageTitle(position: Int): CharSequence? {
        return LoadRouteType.values()[position].title
    }
}
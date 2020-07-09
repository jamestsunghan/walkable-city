package tw.com.walkablecity.event

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import tw.com.walkablecity.event.item.EventItemFragment

class EventAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return EventItemFragment(EventPageType.values()[position])
    }

    override fun getCount(): Int = EventPageType.values().size


    override fun getPageTitle(position: Int): CharSequence? {
        return EventPageType.values()[position].title
    }
}
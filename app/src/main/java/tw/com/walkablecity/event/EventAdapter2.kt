package tw.com.walkablecity.event

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import tw.com.walkablecity.event.item.EventItemFragment

class EventAdapter2(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return EventPageType.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return EventItemFragment(EventPageType.values()[position])
    }


}
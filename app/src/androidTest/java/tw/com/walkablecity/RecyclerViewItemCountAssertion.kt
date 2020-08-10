package tw.com.walkablecity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`

class RecyclerViewItemCountAssertion(val count: Int): ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if(noViewFoundException != null){
            throw noViewFoundException
        }
        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertThat(adapter?.itemCount, `is`(count))
    }
}
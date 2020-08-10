package tw.com.walkablecity


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoadRouteTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION"
        )

    @Test
    fun loadRouteTest() {

        val loginButton = onView(
            allOf(
                withText("登入"),
                childAtPosition(
                    allOf(
                        withId(R.id.signInButton),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        if(Firebase.auth.currentUser == null){
            loginButton.check(ViewAssertions.matches(isDisplayed()))
            loginButton.perform(click())
        } else {
            Thread.sleep(7000)
        }

        val loadRoute = onView(
            allOf(
                withId(R.id.button_load_route), withText("載入路線"),
                childAtPosition(
                    allOf(
                        withId(R.id.pre_walker_zone),
                        childAtPosition(withId(R.id.card_walk_zone), 0)
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        loadRoute.perform(click())
        Thread.sleep(5000)

        val recyclerView = onView(
            allOf(
                withId(R.id.recycler_route_item), isDisplayed(),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    0
                )
            )
        )
        recyclerView.perform(scrollToPosition<ViewHolder>(9))
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(9, click()))
        Thread.sleep(5000)

        val startButton = onView(
            allOf(
                withId(R.id.pre_button_start_stop), withText("開始散步"),
                childAtPosition(
                    allOf(
                        withId(R.id.pre_walker_zone),
                        childAtPosition(withId(R.id.card_walk_zone), 0)
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        startButton.perform(click())
        Thread.sleep(5000)

        val pauseButton = onView(
            allOf(
                withId(R.id.button_pausing),
                childAtPosition(
                    allOf(
                        withId(R.id.walker_zone),
                        childAtPosition(withId(R.id.card_walk_zone), 1)
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        pauseButton.perform(click())
        Thread.sleep(5000)
        pauseButton.check(ViewAssertions.matches(withText("繼續散步")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}

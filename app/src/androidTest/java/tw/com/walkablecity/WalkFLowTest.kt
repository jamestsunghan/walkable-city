package tw.com.walkablecity


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
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
class WalkFLowTest {

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
    fun mainActivityTest() {

        val loginButton = onView(
            allOf(
                withText("登入"),
                childAtPosition(allOf(withId(R.id.signInButton),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 3)
                    ),
                    0
                )
            )
        )
        if(Firebase.auth.currentUser == null){
            loginButton.check(matches(isDisplayed()))
            loginButton.perform(click())
        } else {
            Thread.sleep(10000)
        }

        val startWalking = onView(
            allOf(
                withId(R.id.pre_button_start_stop), withText("開始散步"),
                childAtPosition(
                    allOf(
                        withId(R.id.pre_walker_zone),
                        childAtPosition(
                            withId(R.id.card_walk_zone),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )

        startWalking.perform(click())
        Thread.sleep(2000)

        val pauseResumeButton = onView(
            allOf(
                withId(R.id.button_pausing),
                childAtPosition(
                    allOf(
                        withId(R.id.walker_zone),
                        childAtPosition(
                            withId(R.id.card_walk_zone),
                            1
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        pauseResumeButton.perform(click())
        Thread.sleep(2000)

        pauseResumeButton.perform(click())
        Thread.sleep(5000)
        pauseResumeButton.check(matches(withText("暫停散步")))

        val endWalking = onView(
            allOf(
                withId(R.id.button_start_stop), withText("結束散步"),
                childAtPosition(
                    allOf(
                        withId(R.id.walker_zone),
                        childAtPosition(
                            withId(R.id.card_walk_zone),
                            1
                        )
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        endWalking.perform(click())
        Thread.sleep(6000)

        val noThanks = onView(
            allOf(
                withId(R.id.not_create_route), withText("不用了"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.card_invitation),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        noThanks.perform(click())
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

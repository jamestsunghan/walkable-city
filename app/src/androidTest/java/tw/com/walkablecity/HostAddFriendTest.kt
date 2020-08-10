package tw.com.walkablecity


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
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
class HostAddFriendTest {

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
    fun hostAddFriendTest() {
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
            Thread.sleep(5000)
        } else {
            Thread.sleep(7000)
        }

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.event), withContentDescription("活動"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_nav),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())
        Thread.sleep(5000)

        val floatingActionButton = onView(
            allOf(
                withId(R.id.create_event),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())
        Thread.sleep(5000)

        val appCompatImageView = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("android.widget.ScrollView")),
                    0
                ),
                17
            )
        )
        appCompatImageView.perform(scrollTo(), click())
        Thread.sleep(5000)

        val firstCheckBox = onView(
            allOf(
                withId(R.id.friend_checkbox),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recycler_friend),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        firstCheckBox.perform(click())
        Thread.sleep(100)

        val secondCheckBox = onView(
            allOf(
                withId(R.id.friend_checkbox),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recycler_friend),
                        1
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        secondCheckBox.perform(click())
        Thread.sleep(200)

        val thirdCheckBox = onView(
            allOf(
                withId(R.id.friend_checkbox),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recycler_friend),
                        2
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        thirdCheckBox.perform(click())
        Thread.sleep(5000)

        val materialTextView = onView(
            allOf(
                withId(R.id.friend_selected), withText("邀請好友"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())
        Thread.sleep(300)

        val recyclerView = onView(
            allOf(
                withId(R.id.recycler_friend_adding),
                isDisplayed()
            )
        )

        recyclerView.check(RecyclerViewItemCountAssertion(3))

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

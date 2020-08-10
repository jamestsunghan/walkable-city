package tw.com.walkablecity


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
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
class WalkFlowWithRatingTest {

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
    fun walkFlowWithRatingTest() {
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
                        childAtPosition(
                            withId(R.id.card_walk_zone),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        loadRoute.perform(click())
        Thread.sleep(3000)

        val recyclerView = onView(
            allOf(
                withId(R.id.recycler_route_item), isDisplayed(),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    0
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(9, click()))
        Thread.sleep(3000)

        val preWalkButton = onView(
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

        preWalkButton.perform(click())
        Thread.sleep(3000)

        val endButton = onView(
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
        endButton.perform(click())
        Thread.sleep(5000)

        val materialTextView4 = onView(
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
        materialTextView4.perform(click())
        Thread.sleep(3000)

        val scrollView = onView(
            allOf(
                withId(R.id.rating_scroll),
                isDisplayed()
            )
        )
        scrollView.perform(swipeUp())

        val textInputEditText = onView(

            allOf(
                withId(R.id.edit_route_comment_text),
                isDisplayed()
            )
        )

        Thread.sleep(1500)

        textInputEditText.perform(click(), replaceText("很愜意的一條路"), closeSoftKeyboard())

        Thread.sleep(3000)
//        pressBack()

        val materialTextView5 = onView(
            allOf(
                withId(R.id.send_rating), withText("完成評價"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.viewpager_rating),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialTextView5.perform(click())
        Thread.sleep(3000)
        preWalkButton.check(matches(isDisplayed()))
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

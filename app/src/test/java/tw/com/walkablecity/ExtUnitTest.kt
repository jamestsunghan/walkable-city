package tw.com.walkablecity

import android.content.Context
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import tw.com.walkablecity.data.RouteRating
import tw.com.walkablecity.data.RouteSorting
import tw.com.walkablecity.ext.toSortList
import kotlin.math.roundToInt

@RunWith(MockitoJUnitRunner::class)
class ExtUnitTest {

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun initMocks(){

    }

//    @Test
//    fun ratingSort_isCorrect(){
//        val rating = RouteRating(
//            tranquility = 3.3f,
//            vibe        = 3.5f,
//            scenery     = 3.8f,
//            snack       = 4.5f,
//            rest        = 4.9f,
//            coverage    = 3.9f
//        )
//        val sorting = RouteSorting.SNACK
//
//        val expectedList = listOf(
//            "饞 | 4.5", "歇 | 4.9", "蔭 | 3.9", "景 | 3.8", "逛 | 3.5", "靜 | 3.3"
//        )
//        Assert.assertEquals(expectedList, rating.toSortList(sorting))
//    }

    @Test
    fun toNewAverage_isCorrect(){
        val amount = 9
        val ratingAvr = RouteRating(
            tranquility = 3f,
            vibe        = 5f,
            scenery     = 4f,
            snack       = 5f,
            rest        = 4f,
            coverage    = 4f
        )
        val newRating = RouteRating(
            tranquility = 3f,
            vibe        = 3f,
            scenery     = 3f,
            snack       = 4f,
            rest        = 4f,
            coverage    = 3f
        )

        val expectRating = RouteRating(
            tranquility = 3f,
            vibe        = 4.8f,
            scenery     = 3.9f,
            snack       = 4.9f,
            rest        = 4f,
            coverage    = 3.9f
        )

        Assert.assertEquals(expectRating, ratingAvr.addToAverage(newRating, amount))
    }


}
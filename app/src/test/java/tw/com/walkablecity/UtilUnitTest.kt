package tw.com.walkablecity

import org.junit.Assert
import org.junit.Test
import tw.com.walkablecity.util.Util

class UtilUnitTest {

    @Test
    fun padStart_isCorrect() {

        val input = 1L
        val output = Util.lessThenTenPadStart(input)

        Assert.assertEquals("01", output)
    }

    @Test
    fun addMonth_isCorrect() {

        val input = "2020-05-01"
        val output = Util.dateAddMonth(input)

        Assert.assertEquals("2020-06-01", output)
    }

    @Test
    fun addMonthCrossYear_isCorrect() {

        val input = "2020-12-01"
        val output = Util.dateAddMonth(input)

        Assert.assertEquals("2021-01-01", output)
    }

    @Test
    fun addMonth_isWrong() {

        val input = "2020-12-01"
        val output = Util.dateAddMonth(input)

        Assert.assertNotEquals("2021-12-01", output)
    }

    @Test
    fun displaySliderValue_isCorrect() {

        val values = listOf(2f,4f)
        val max = 6f
        val output = Util.displaySliderValue(values, max)

        Assert.assertEquals("2 ~ 4", output)
    }

    @Test
    fun displaySliderValueMax_isCorrect() {

        val values = listOf(10f,100f)
        val max = 100f
        val output = Util.displaySliderValue(values, max)

        Assert.assertEquals("10 ~ 100+", output)
    }

}
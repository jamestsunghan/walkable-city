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

}
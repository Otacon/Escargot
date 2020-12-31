package protocol.notification

import protocol.Status
import org.junit.Assert.assertEquals
import org.junit.Test

class CommandParserNlnTest {

    private val parser = CommandParserNln()

    @Test
    fun parse() {
        val actual = parser.parse("NLN NLN 1:orfeo.ciano@gmail.com Cyanotic%20Test 0:0 %3Cmsnobj%2F%3E\n")
        val expected = NotificationReceiveCommand.NLN(
            status = Status.ONLINE,
            passport = "orfeo.ciano@gmail.com",
            displayName = "Cyanotic Test",
            networkId = "0:0",
            msnObj = "<msnobj/>"
        )

        assertEquals(expected, actual)
    }
}
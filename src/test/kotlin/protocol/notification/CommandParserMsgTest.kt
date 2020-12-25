package protocol.notification

import org.junit.Test

import org.junit.Assert.*

class CommandParserMsgTest {

    val parser = CommandParserMsg()

    @Test
    fun parse() {
        val actual = parser.parse("MSG Hotmail Hotmail 1444")
        val expected = NotificationReceiveCommand.MSG("Hotmail", "Hotmail", 1444)

        assertEquals(expected, actual)
    }
}
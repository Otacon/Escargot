package protocol.commands

import org.junit.Test

import org.junit.Assert.*

class CommandParserUserSSOAckTest {

    private val parser = CommandParserUserSSOAck()

    @Test
    fun parse() {
        val actual = parser.parse("USR 4 OK email@email.com 1 0")
        val expected = ReceiveCommand.USRSSOAck(4, "email@email.com", isVerified = true, isKid = false)

        assertEquals(expected, actual)
    }
}
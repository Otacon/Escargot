package protocol.commands

import org.junit.Test

import org.junit.Assert.*

class CommandParserUserSSOStatusTest {

    private val parser = CommandParserUserSSOStatus()

    @Test
    fun parse() {
        val actual = parser.parse("USR 4 SSO S MBI_KEY_OLD NONCE")
        val expected = ParseResult.Success(ReceiveCommand.USRSSOStatus(4, "NONCE"))
        assertEquals(expected, actual)
    }
}
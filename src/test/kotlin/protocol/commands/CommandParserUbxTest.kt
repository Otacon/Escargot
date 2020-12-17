package protocol.commands

import org.junit.Test

import org.junit.Assert.*

class CommandParserUbxTest {

    private val parser = CommandParserUbx()

    @Test
    fun parse() {
        val actual = parser.parse("UBX 1:email@email.com 0")
        val expected = ParseResult.Success(ReceiveCommand.UBX(1, "email@email.com", 0))

        assertEquals(expected, actual)
    }
}
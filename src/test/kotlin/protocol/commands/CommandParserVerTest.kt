package protocol.commands

import org.junit.Test

import org.junit.Assert.*

class CommandParserVerTest {

    private val parser = CommandParserVer()

    @Test
    fun parse() {
        val actual = parser.parse("VER 1 MSNP18 blah")
        val expected = ParseResult.Success(ReceiveCommand.VER(1, listOf(ProtocolVersion.MSNP18, ProtocolVersion.UNKNOWN)))

        assertEquals(expected, actual)
    }
}
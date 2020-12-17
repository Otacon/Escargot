package protocol.commands

import junit.framework.Assert.assertEquals
import org.junit.Test

internal class CommandParserGcfTest {

    private val parser: CommandParserGcf = CommandParserGcf()

    @Test
    fun parse_success() {
        val actual = parser.parse("GCF 0 1187")
        val expected = ParseResult.Success(ReceiveCommand.GCF(1187))
        assertEquals(expected, actual)
    }
}
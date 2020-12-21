package protocol.commands

import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommandParserChgTest {

    private val parser = CommandParserChg()

    @Test
    fun parse_success() {
        val actual = parser.parse("CHG 5 NLN 0 0")
        val receiveCommand = ReceiveCommand.CHG(
            sequence = 5,
            status = "NLN",
            capabilities = "0",
            msnObj = "0"
        )
        val expected = ParseResult.Success(receiveCommand)
        assertEquals(expected, actual)
    }
}
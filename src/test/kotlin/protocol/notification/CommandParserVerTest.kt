package protocol.notification

import org.junit.Assert.assertEquals
import org.junit.Test
import protocol.ProtocolVersion

class CommandParserVerTest {

    private val parser = CommandParserVer()

    @Test
    fun parse() {
        val actual = parser.parse("VER 1 MSNP18 blah")
        val expected = NotificationReceiveCommand.VER(1, listOf(ProtocolVersion.MSNP18, ProtocolVersion.UNKNOWN))

        assertEquals(expected, actual)
    }
}
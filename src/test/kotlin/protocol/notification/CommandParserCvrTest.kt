package protocol.notification

import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommandParserCvrTest {

    private val parser = CommandParserCvr()

    @Test
    fun parse_success() {
        val actual = parser.parse("CVR 2 1.0.0 1.0.0 1.0.0 https://escargot.log1p.xyz https://escargot.log1p.xyz")
        val expected = NotificationReceiveCommand.CVR(
            sequence = 2,
            minVersion = "1.0.0",
            recommendedVersion = "1.0.0",
            downloadUrl = "https://escargot.log1p.xyz",
            infoUrl = "https://escargot.log1p.xyz"
        )
        assertEquals(expected, actual)
    }
}
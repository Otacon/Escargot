package org.cyanotic.butterfly.protocol.notification

import org.junit.Assert.assertEquals
import org.junit.Test

internal class CommandParserGcfTest {

    private val parser: CommandParserGcf = CommandParserGcf()

    @Test
    fun parse_success() {
        val actual = parser.parse("GCF 0 1187")
        val expected = NotificationReceiveCommand.GCF(0,1187)
        assertEquals(expected, actual)
    }
}
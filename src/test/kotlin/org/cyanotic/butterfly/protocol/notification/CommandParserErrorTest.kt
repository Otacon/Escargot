package org.cyanotic.butterfly.protocol.notification

import org.junit.Assert.*
import org.junit.Test

internal class CommandParserErrorTest{

    private val parser = CommandParserError()

    @Test
    fun parse_success() {
        val actual = parser.parse("911 1")
        val expected = NotificationReceiveCommand.Error(
            code = 911,
            sequence = 1
        )
        assertEquals(expected, actual)
    }
}
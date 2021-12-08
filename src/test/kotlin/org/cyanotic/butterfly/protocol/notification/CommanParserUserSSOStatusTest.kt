package org.cyanotic.butterfly.protocol.notification

import org.junit.Assert.assertEquals
import org.junit.Test

class CommandParserUserSSOStatusTest {

    private val parser = CommandParserUserSSOStatus()

    @Test
    fun parse() {
        val actual = parser.parse("USR 4 SSO S MBI_KEY_OLD NONCE")
        val expected = NotificationReceiveCommand.USRSSOStatus(4, "NONCE")
        assertEquals(expected, actual)
    }
}
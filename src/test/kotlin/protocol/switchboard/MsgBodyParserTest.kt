package protocol.switchboard

import org.junit.Assert.assertEquals
import org.junit.Test


internal class MsgBodyParserTest {

    val parser = MsgBodyParser()

    @Test
    fun parseMessage() {
        val body = "MIME-Version: 1.0\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "X-MMS-IM-Format: FN=MS%20Sans%20Serif; EF=; CO=0; CS=0; PF=0\r\n\r\nBody"
        val actual = parser.parse(body)
        val expected = MsgBody.Message("Body")
        assertEquals(expected, actual)
    }

    @Test
    fun parseTyping() {
        val body = "MIME-Version: 1.0\r\n" +
                "Content-Type: text/x-msmsgscontrol\r\n" +
                "TypingUser: bob@passport.com\r\n"
        val actual = parser.parse(body)
        val expected = MsgBody.Typing("bob@passport.com")
        assertEquals(expected, actual)
    }
}
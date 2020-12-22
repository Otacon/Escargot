package protocol.commands

import org.junit.Assert.assertEquals
import org.junit.Test

class CommandParserRngTest {

    val parser = CommandParserRng()

    @Test
    fun parse() {
        val actual =
            parser.parse("RNG a0e59d8e1f31765a2799187cc9f9037 m1.escargot.log1p.xyz:1864 CKI d8cb7cf1a6d5060df3dd orfeo18@hotmail.it Cyanotic U messenger.hotmail.com 1")
        val expected = ParseResult.Success(
            ReceiveCommand.RNG(
                sessionId = "a0e59d8e1f31765a2799187cc9f9037",
                address = "m1.escargot.log1p.xyz:1864",
                authType = "CKI",
                ticket = "d8cb7cf1a6d5060df3dd",
                passport = "orfeo18@hotmail.it",
                inviteName = "Cyanotic"
            )
        )

        assertEquals(expected, actual)
    }
}
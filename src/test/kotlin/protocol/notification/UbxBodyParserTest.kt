package protocol.notification

import org.junit.Assert.assertEquals
import org.junit.Test

class UbxBodyParserTest {

    val parser = UbxBodyParser()

    @Test
    fun parse() {
        val body = """
            <Data>
                <PSM>PersonalMessage</PSM>
                <CurrentMedia>CurrentMedia</CurrentMedia>
                <DDP>DDP</DDP>
                <SignatureSound>SignatureSound</SignatureSound>
                <Scene>Scene</Scene>
                <ColorScheme>ColorScheme</ColorScheme>
                <EndpointData id="{c597a8fc-526f-4954-96e3-eb98f62c789e}">
                    <Capabilities>0:0</Capabilities>
                </EndpointData>
                <PrivateEndpointData id="{c597a8fc-526f-4954-96e3-eb98f62c789e}"></PrivateEndpointData>
            </Data>
        """.trimIndent()

        val actual = parser.parse(body)
        val expected = UbxBodyData(
            personalMessage = "PersonalMessage",
            currentMedia = "CurrentMedia",
            ddp = "DDP",
            signatureSound = "SignatureSound",
            scene = "Scene",
            colorScheme = "ColorScheme"
        )
        assertEquals(expected, actual)
    }
}
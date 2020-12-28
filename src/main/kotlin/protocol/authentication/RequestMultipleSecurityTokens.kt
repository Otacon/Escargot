package protocol.authentication

import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import java.io.StringWriter

class RequestMultipleSecurityTokensRequestFactory {

    fun createRequest(username: String, password: String): String {
        val persister: Serializer = Persister()
        val writer = StringWriter()
        writer.write("<?xml version=\"1.0\"?>\n")
        val request = RequestMultipleSecurityTokensEnvelope(
            RequestMultipleSecurityTokensHead(
                RequestMultipleSecurityTokensAuthInfo(
                    id = "PPAuthInfo",
                    hostingApp = "{7108E71A-9926-4FCB-BCC9-9A9D3F32E423}",
                    binaryVersion = "4",
                    uiVersion = "1",
                    cookies = "",
                    requestParams = "AQAAAAIAAABsYwQAAAAxMDMz"
                ),
                RequestMultipleSecurityTokensSecurity(
                    usernameToken = RequestMultipleSecurityTokensUsernameToken(
                        id = "user",
                        username = username,
                        password = password
                    )
                )
            ),
            RequestMultipleSecurityTokensBody(
                securityTokens = RequestMultipleSecurityTokens(
                    id = "RSTS",
                    tokens = listOf(
                        RequestSecurityToken(
                            id = "RST0",
                            requestType = "http://schemas.xmlsoap.org/ws/2004/04/security/trust/Issue",
                            address = "http://Passport.NET/tb",
                            policyReference = null
                        ),
                        RequestSecurityToken(
                            id = "RSTn",
                            requestType = "http://schemas.xmlsoap.org/ws/2004/04/security/trust/Issue",
                            address = "messengerclear.live.com",
                            policyReference = PolicyReference(
                                uri = "policy parameter"
                            )
                        )
                    )
                )
            )
        )
        persister.write(request, writer)
        return writer.buffer.toString()
    }
}

@Root(name = "Envelope")
@NamespaceList(
    Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/"),
    Namespace(reference = "http://schemas.xmlsoap.org/ws/2003/06/secext", prefix = "wsse"),
    Namespace(reference = "urn:oasis:names:tc:SAML:1.0:assertion", prefix = "saml"),
    Namespace(reference = "http://schemas.xmlsoap.org/ws/2002/12/policy", prefix = "wsp"),
    Namespace(
        reference = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
        prefix = "wsu"
    ),
    Namespace(reference = "http://schemas.xmlsoap.org/ws/2004/03/addressing", prefix = "wsa"),
    Namespace(reference = "http://schemas.xmlsoap.org/ws/2004/04/sc", prefix = "wssc"),
    Namespace(reference = "http://schemas.xmlsoap.org/ws/2004/04/trust", prefix = "wst")
)
data class RequestMultipleSecurityTokensEnvelope(

    @field:Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
    @field:Element(name = "Header") val header: RequestMultipleSecurityTokensHead,

    @field:Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
    @field:Element(name = "Body") val body: RequestMultipleSecurityTokensBody

)

data class RequestMultipleSecurityTokensHead(
    @field:Element(name = "AuthInfo") val authInfo: RequestMultipleSecurityTokensAuthInfo,
    @field:Element(name = "wsse:Security") val security: RequestMultipleSecurityTokensSecurity
)


@Namespace(reference = "http://schemas.microsoft.com/Passport/SoapServices/PPCRL", prefix = "ps")
data class RequestMultipleSecurityTokensAuthInfo(
    @field:Attribute(name = "Id") val id: String,
    @field:Element(name = "ps:HostingApp") val hostingApp: String,
    @field:Element(name = "ps:BinaryVersion") val binaryVersion: String,
    @field:Element(name = "ps:UIVersion") val uiVersion: String,
    @field:Element(name = "ps:Cookies") val cookies: String,
    @field:Element(name = "ps:RequestParams") val requestParams: String
)


data class RequestMultipleSecurityTokensSecurity(
    @field:Element(name = "wsse:UsernameToken")
    val usernameToken: RequestMultipleSecurityTokensUsernameToken
)


data class RequestMultipleSecurityTokensUsernameToken(
    @field:Attribute(name = "Id") val id: String,
    @field:Element(name = "wsse:Username") val username: String,
    @field:Element(name = "wsse:Password") val password: String,
)

data class RequestMultipleSecurityTokensBody(
    @field:Element(name = "RequestMultipleSecurityTokens") val securityTokens: RequestMultipleSecurityTokens,
)

@Namespace(reference = "http://schemas.microsoft.com/Passport/SoapServices/PPCRL", prefix = "ps")
data class RequestMultipleSecurityTokens(
    @field:Attribute(name = "Id") val id: String,
    @field:ElementList(inline = true) val tokens: List<RequestSecurityToken>
)

@Root(name = "wst:RequestSecurityToken")
data class RequestSecurityToken(
    @field:Attribute(name = "Id") val id: String,
    @field:Element(name = "wst:RequestType") val requestType: String,

    @field:Path("wsp:AppliesTo/wsa:EndpointReference")
    @field:Element(name = "wsa:Address") val address: String,

    @field:Element(name = "wsse:PolicyReference", required = false) val policyReference: PolicyReference?
)

data class PolicyReference(
    @field:Attribute(name = "URI") val uri: String
)
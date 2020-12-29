package protocol.soap

import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister

class RequestSecurityTokenParser {

    fun parse(xml: String): SecurityToken? {
        try {
            val serializer: Serializer = Persister()
            val parsedData = serializer.read(RequestSecurityTokenEnvelope::class.java, xml)
            parsedData.body.tokens.firstOrNull { it.tokenType == "urn:passport:compact" }?.let {
                val secret = it.requestedProofToken?.secret
                it.requestedSecurityToken.binarySecurityToken?.value?.let { binarySecurityToken ->
                    val regex = Regex("""t=(.*)&p=""")
                    val result = regex.find(binarySecurityToken)
                    result?.let { match ->
                        val nonce = match.groupValues[1]
                        return SecurityToken(nonce, secret!!)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

data class SecurityToken(
    val nonce: String,
    val secret: String
)

@Root(name = "Envelope", strict = false)
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "S")
data class RequestSecurityTokenEnvelope(

    @field:Element(name = "Header")
    @param:Element(name = "Header")
    val header: RequestSecurityTokenResponseHeader,

    @field:Element(name = "Body")
    @param:Element(name = "Body")
    val body: RequestSecurityTokenResponseBody
)

@Namespace(reference = "http://schemas.microsoft.com/Passport/SoapServices/SOAPFault", prefix = "psf")
@Root(strict = false)
data class RequestSecurityTokenResponseHeader(

    @field:Path("pp")
    @param:Path("pp")
    @field:Element(name = "serverVersion")
    @param:Element(name = "serverVersion")
    val serverVersion: String,

    @field:Path("pp")
    @param:Path("pp")
    @field:Element(name = "PUID")
    @param:Element(name = "PUID")
    val puid: String,

    @field:Path("pp")
    @param:Path("pp")
    @field:Element(name = "configVersion")
    @param:Element(name = "configVersion")
    val configVersion: String,

    @field:Path("pp")
    @param:Path("pp")
    @field:Element(name = "uiVersion")
    @param:Element(name = "uiVersion")
    val uiVersion: String,

    @field:Path("pp")
    @param:Path("pp")
    @field:Element(name = "mobileConfigVersion")
    @param:Element(name = "mobileConfigVersion")
    val mobileConfigVersion: String,

    @field:Path("pp")
    @param:Path("pp")
    @field:Element(name = "authstate")
    @param:Element(name = "authstate")
    val authState: String,

    @field:Path("pp")
    @param:Path("pp")
    @field:Element(name = "reqstatus")
    @param:Element(name = "reqstatus")
    val reqStatus: String,

    @field:Path("pp")
    @param:Path("pp")
    @field:Element(name = "serverInfo")
    @param:Element(name = "serverInfo")
    val serverInfo: RequestSecurityTokenResponseServerInfo,

    @field:Path("pp")
    @param:Path("pp")
    @field:ElementList(name = "browserCookies")
    @param:ElementList(name = "browserCookies")
    val browserCookies: List<BrowserCookie>,

    @field:Path("pp")
    @param:Path("pp")
    @field:ElementList(name = "credProperties")
    @param:ElementList(name = "credProperties")
    val credProperties: List<CredProperty>,

    @field:Path("pp")
    @param:Path("pp")
    @field:ElementList(name = "extProperties")
    @param:ElementList(name = "extProperties")
    val extProperties: List<ExtProperty>

)

data class RequestSecurityTokenResponseServerInfo(

    @field:Attribute(name = "Path")
    @param:Attribute(name = "Path")
    val path: String,

    @field:Attribute(name = "RollingUpgradeState")
    @param:Attribute(name = "RollingUpgradeState")
    val rollingUpgradeState: String,

    @field:Attribute(name = "LocVersion")
    @param:Attribute(name = "LocVersion")
    val locVersion: String,

    @field:Attribute(name = "ServerTime")
    @param:Attribute(name = "ServerTime")
    val serverTime: String,

    @field:Text
    @param:Text
    val value: String
)

data class BrowserCookie(
    @field:Attribute(name = "Name")
    @param:Attribute(name = "Name")
    val name: String,

    @field:Attribute(name = "URL")
    @param:Attribute(name = "URL")
    val url: String,

    @field:Text
    @param:Text
    val value: String
)

data class CredProperty(
    @field:Attribute(name = "Name")
    @param:Attribute(name = "Name")
    val name: String,

    @field:Text(required = false)
    @param:Text(required = false)
    val value: String?
)

data class ExtProperty(
    @field:Attribute(name = "Name")
    @param:Attribute(name = "Name")
    val name: String,

    @field:Attribute(name = "Expiry", required = false)
    @param:Attribute(name = "Expiry", required = false)
    val expiry: String?,

    @field:Attribute(name = "Domains", required = false)
    @param:Attribute(name = "Domains", required = false)
    val domains: String?,

    @field:Attribute(name = "IgnoreRememberMe", required = false)
    @param:Attribute(name = "IgnoreRememberMe", required = false)
    val ignoreRememberMe: Boolean?,

    @field:Text
    @param:Text
    val value: String
)

data class RequestSecurityTokenResponseBody(
    @field:ElementList(name = "RequestSecurityTokenResponseCollection")
    @param:ElementList(name = "RequestSecurityTokenResponseCollection")
    val tokens: List<RequestSecurityTokenResponse>
)

data class RequestSecurityTokenResponse(

    @field:Element(name = "TokenType")
    @param:Element(name = "TokenType")
    val tokenType: String,

    @field:Path(value = "AppliesTo/EndpointReference")
    @param:Path(value = "AppliesTo/EndpointReference")
    @field:Element(name = "Address")
    @param:Element(name = "Address")
    val appliesToEndpoint: String,

    @field:Element(name = "LifeTime")
    @param:Element(name = "LifeTime")
    val lifetime: LifeTime,

    @field:Element(name = "RequestedSecurityToken")
    @param:Element(name = "RequestedSecurityToken")
    val requestedSecurityToken: RequestedSecurityToken,

    @field:Element(name = "RequestedTokenReference", required = false)
    @param:Element(name = "RequestedTokenReference", required = false)
    val tokenReference: RequestedTokenReference?,

    @field:Element(name = "RequestedProofToken", required = false)
    @param:Element(name = "RequestedProofToken", required = false)
    val requestedProofToken: RequestedProofToken?
)

data class LifeTime(
    @field:Element(name = "Created")
    @param:Element(name = "Created")
    val created: String,

    @field:Element(name = "Expires")
    @param:Element(name = "Expires")
    val expires: String,
)

data class RequestedSecurityToken(
    @field:Element(name = "BinarySecurityToken", required = false)
    @param:Element(name = "BinarySecurityToken", required = false)
    val binarySecurityToken: BinarySecurityToken?,

    @field:Element(name = "EncryptedData", required = false)
    @param:Element(name = "EncryptedData", required = false)
    val encryptedData: EncryptedData?,
)

data class EncryptedData(
    @field:Attribute(name = "Id")
    @param:Attribute(name = "Id")
    val id: String,

    @field:Attribute(name = "Type")
    @param:Attribute(name = "Type")
    val type: String,

    @field:Path(value = "EncryptionMethod")
    @param:Path(value = "EncryptionMethod")
    @field:Attribute(name = "Algorithm")
    @param:Attribute(name = "Algorithm")
    val algorithm: String,

    @field:Path(value = "KeyInfo")
    @param:Path(value = "KeyInfo")
    @field:Element(name = "KeyName")
    @param:Element(name = "KeyName")
    val keyName: String,

    @field:Path(value = "CipherData")
    @param:Path(value = "CipherData")
    @field:Element(name = "CipherValue")
    @param:Element(name = "CipherValue")
    val cypherData: String
)

data class BinarySecurityToken(
    @field:Attribute(name = "Id")
    @param:Attribute(name = "Id")
    val id: String,

    @field:Text
    @param:Text
    val value: String
)

data class RequestedTokenReference(
    @field:Path(value = "KeyIdentifier")
    @param:Path(value = "KeyIdentifier")
    @field:Attribute(name = "ValueType")
    @param:Attribute(name = "ValueType")
    val valueType: String,

    @field:Path(value = "Reference")
    @param:Path(value = "Reference")
    @field:Attribute(name = "URI")
    @param:Attribute(name = "URI")
    val referenceUri: String
)

data class RequestedProofToken(
    @field:Path(value = "BinarySecret")
    @param:Path(value = "BinarySecret")
    @field:Text
    @param:Text
    val secret: String
)
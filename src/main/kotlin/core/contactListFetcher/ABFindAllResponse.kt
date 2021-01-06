package core.contactListFetcher

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root
data class AbFindAllResponseEnvelope(
    @field:Element(name = "Header")
    @param:Element(name = "Header")
    val header: AbFindAllResponseHeader,

    @field:Element(name = "Body")
    @param:Element(name = "Body")
    val body: AbFindAllResponseBody
)

@Root
data class AbFindAllResponseHeader(
    @field:Element(name = "ServiceHeader")
    @param:Element(name = "ServiceHeader")
    val serviceHeader: AbFindAllResponseServiceHeader
)

@Root
data class AbFindAllResponseBody(
    @field:Element(name = "ABFindAllResponse")
    @param:Element(name = "ABFindAllResponse")
    val findAllResponse: AbFindAllResponseBodyResponse
)

@Root
data class AbFindAllResponseBodyResponse(
    @field:Element(name = "ABFindAllResult")
    @param:Element(name = "ABFindAllResult")
    val findAllResponse: AbFindAllResponseResult
)

@Root(strict = false)
data class AbFindAllResponseResult(
    @field:ElementList(name = "contacts")
    @param:ElementList(name = "contacts")
    val findAllResponse: List<AbFindAllContact>
)

@Root(strict = false)
data class AbFindAllContact(
    @field:Element(name = "contactId")
    @param:Element(name = "contactId")
    val contactId: String,

    @field:Element(name = "contactInfo")
    @param:Element(name = "contactInfo")
    val contactInfo: AbFindAllContactInfo
)

@Root(strict = false)
data class AbFindAllContactInfo(

    @field:Element(name = "quickName")
    @param:Element(name = "quickName")
    val quickName: String,

    @field:Element(name = "passportName")
    @param:Element(name = "passportName")
    val passportName: String,

    @field:Element(name = "displayName")
    @param:Element(name = "displayName")
    val displayName: String,

    @field:Element(name = "contactType")
    @param:Element(name = "contactType")
    val contactType: String
)


@Root
data class AbFindAllResponseServiceHeader(

    @field:Element(name = "Version")
    @param:Element(name = "Version")
    val serviceHeader: String,

    @field:Element(name = "CacheKey")
    @param:Element(name = "CacheKey")
    val cacheKey: String,

    @field:Element(name = "CacheKeyChanged")
    @param:Element(name = "CacheKeyChanged")
    val cacheKeyChanged: Boolean,

    @field:Element(name = "PreferredHostName")
    @param:Element(name = "PreferredHostName")
    val preferredHostName: String,

    @field:Element(name = "SessionId")
    @param:Element(name = "SessionId")
    val sessionId: String

)
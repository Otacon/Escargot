package org.cyanotic.butterfly.core.contact_list_fetcher

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Root

@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap"),
    Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
    Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
    Namespace(reference = "http://schemas.xmlsoap.org/soap/encoding/", prefix = "soapenc")
)
data class ABFindAllEnvelope(
    @field:Element(name = "Header")
    @field:Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
    val header: ABFindAllHeader,

    @field:Element(name = "Body")
    @field:Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
    val body: AbFindAllBody
)

@Root
data class ABFindAllHeader(
    @field:Element(name = "ABApplicationHeader") val applicationHeader: AbApplicationHeader,
    @field:Element(name = "ABAuthHeader") val abAuthHeader: AbAuthHeader
)

@Root
@Namespace(reference = "http://www.msn.com/webservices/AddressBook")
data class AbApplicationHeader(
    @field:Element(name = "ApplicationId") val applicationId: String,
    @field:Element(name = "IsMigration") val isMigration: Boolean,
    @field:Element(name = "PartnerScenario") val partnerScenario: String
)

@Root
@Namespace(reference = "http://www.msn.com/webservices/AddressBook")
data class AbAuthHeader(
    @field:Element(name = "ManagedGroupRequest") val managedGroupRequests: Boolean
)

@Root
data class AbFindAllBody(
    @field:Element(name = "ABFindAll") val abFindAll: AbFindAll
)

@Root
@Namespace(reference = "http://www.msn.com/webservices/AddressBook")
data class AbFindAll(
    @field:Element(name = "abId") val abId: String,
    @field:Element(name = "abView") val abView: String,
    @field:Element(name = "deltasOnly") val deltasOnly: Boolean,
    @field:Element(name = "lastChange") val lastChange: String
)
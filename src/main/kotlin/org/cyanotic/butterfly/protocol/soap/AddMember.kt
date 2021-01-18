package org.cyanotic.butterfly.protocol.soap

import org.simpleframework.xml.*

@Root(name = "soap:Envelope")
@NamespaceList(
    Namespace(prefix = "soap", reference = "http://schemas.xmlsoap.org/soap/envelope/"),
    Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance")
)
data class AddContactRequestEnvelope(

    @field:Element(name = "Header")
    @field:Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
    val header: AddContactRequestHeader,

    @field:Element(name = "Body")
    @field:Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
    val body: AddContactRequestBody
)

data class AddContactRequestHeader(
    @field:Element(name = "ABApplicationHeader") val applicationHeader: AddContactRequestABApplicationHeader,
    @field:Element(name = "ABAuthHeader") val abAuthHeader: AddContactRequestAbAuthHeader
)

@Root
@Namespace(reference = "http://www.msn.com/webservices/AddressBook")
data class AddContactRequestABApplicationHeader(
    @field:Element(name = "ApplicationId") val applicationId: String,
    @field:Element(name = "IsMigration") val isMigration: Boolean,
    @field:Element(name = "PartnerScenario") val partnerScenario: String
)

@Root
@Namespace(reference = "http://www.msn.com/webservices/AddressBook")
data class AddContactRequestAbAuthHeader(
    @field:Element(name = "ManagedGroupRequest") val managedGroupRequests: Boolean,
    @field:Element(name = "TicketToken") val ticketToken: String
)

data class AddContactRequestBody(
    @field:Element(name = "ABContactAdd")
    val request: AddContactRequestContent
)

@Namespace(reference = "http://www.msn.com/webservices/AddressBook")
data class AddContactRequestContent(
    @field:Element(name = "abId") val abId: String,
    @field:ElementList(
        name = "contacts",
        entry = "Contact"
    ) val contacts: ArrayList<AddContactRequestContact>
)

data class AddContactRequestContact(
    @field:Element(name = "contactInfo")
    val contactInfo: AddContactRequestContactInfo
)

data class AddContactRequestContactInfo(
    @field:Element(name = "contactType") val type: String,
    @field:Element(name = "isMessengerUser") val isMessengerUser: Boolean,
    @field:Element(name = "passportName") val passportName: String,
    @field:Element(name = "MessengerMemberInfo") val messengerMemberInfo: AddContactRequestMessengerMemberInfo,
)

data class AddContactRequestMessengerMemberInfo(
    @field:Element(name = "DisplayName") val displayName: String,
)
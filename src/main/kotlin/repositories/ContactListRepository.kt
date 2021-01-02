package repositories

import database.MSNDB
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import protocol.Status
import protocol.notification.NotificationTransport
import protocol.notification.NotificationTransportManager
import repositories.profile.ProfileDataSourceLocal
import repositories.profile.ProfileRepository
import repositories.profile.ProfileRepositoryFactory
import java.io.StringWriter

class ContactListRepositoryFactory {

    fun createContactListRepository(): ContactListRepository {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY })
            .build()
        return ContactListRepository(
            okHttpClient,
            ProfileDataSourceLocal(MSNDB.db),
            NotificationTransportManager.transport
        )
    }
}

class ContactListRepository(
    private val okHttpClient: OkHttpClient,
    private val profileDataSource: ProfileDataSourceLocal,
    private val transport: NotificationTransport
) {

    val contactChanged = transport.contactChanged()

    suspend fun getContacts(): List<AbFindAllContact> {
        val persister: Serializer = Persister()
        val example = ABFindAllEnvelope(
            ABFindAllHeader(
                AbApplicationHeader(
                    applicationId = "CFE80F9D-180F-4399-82AB-413F33A1FA11",
                    isMigration = false,
                    partnerScenario = "Initial"
                ),
                AbAuthHeader(
                    managedGroupRequests = false
                )
            ),
            AbFindAllBody(
                AbFindAll(
                    abId = "00000000-0000-0000-0000-000000000000",
                    abView = "Full",
                    deltasOnly = false,
                    lastChange = "0001-01-01T00:00:00.0000000-08:00"
                )
            )
        )
        val writer = StringWriter()
        writer.write("<?xml version=\"1.0\"?>\n")
        persister.write(example, writer)
        val soapRequestBody = writer.buffer.toString()
        val request = Request.Builder()
            .url("https://m1.escargot.log1p.xyz/abservice/abservice.asmx")
            .post(body = soapRequestBody.toRequestBody("text/xml".toMediaType()))
            .addHeader("SOAPAction", "http://www.msn.com/webservices/AddressBook/ABFindAll")
            .addHeader("Cookie", "MSPAuth=${profileDataSource.getMsnpAuth()}")
            .build()
        val response = okHttpClient.newCall(request).execute()

        val serializer: Serializer = Persister()
        val soapResponseBody = response.body!!.string()
        val contacts = serializer.read(AbFindAllResponseEnvelope::class.java, soapResponseBody)
        return contacts.body.findAllResponse.findAllResponse.findAllResponse
    }

}


//REQUEST
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

//RESPONSE
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

data class Contact(
    var passport: String,
    var nickname: String,
    var status: Status,
    var personalMessage: String
)
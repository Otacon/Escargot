package repositories.contactList

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import protocol.notification.NotificationTransport
import repositories.*
import repositories.contactList.data.*
import java.io.StringWriter

class ContactListDataSourceRemote(
    private val okHttpClient: OkHttpClient,
    private val trasport: NotificationTransport
) {

    suspend fun contactChanged() = trasport.contactChanged()

    suspend fun getContacts(mspAuth: String): List<AbFindAllContact> {
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
            .addHeader("Cookie", "MSPAuth=$mspAuth")
            .build()
        val response = okHttpClient.newCall(request).execute()

        val serializer: Serializer = Persister()
        val soapResponseBody = response.body!!.string()
        val contacts = serializer.read(AbFindAllResponseEnvelope::class.java, soapResponseBody)
        return contacts.body.findAllResponse.findAllResponse.findAllResponse
    }
}
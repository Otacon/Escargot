package repositories.profile

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import protocol.Endpoints
import protocol.ProtocolVersion
import protocol.Status
import protocol.authentication.RequestMultipleSecurityTokensRequestFactory
import protocol.notification.NotificationSendCommand
import protocol.notification.NotificationTransport
import protocol.notification.NotificationTransportManager
import protocol.notification.TransportException
import protocol.security.TicketEncoder
import protocol.soap.RequestSecurityTokenParser
import protocol.utils.SystemInfoRetriever
import java.io.IOException
import java.util.*


class ProfileDataSourceRemote(
    private val systemInfoRetriever: SystemInfoRetriever,
    private val transport: NotificationTransport,
    private val okHttpClient: OkHttpClient,
    private val ticketEncoder: TicketEncoder,
    private val multipleSecurityTokensRequestFactory: RequestMultipleSecurityTokensRequestFactory,
    private val requestSecurityTokenParser: RequestSecurityTokenParser
) {
    var clientName = "Escargot Messenger"
    var clientVersion = "1.0 (in-dev)"

    suspend fun authenticate(username: String, password: String): AuthenticationResult {
        transport.connect()

        val verResponse = transport.sendVer(protocols = listOf(ProtocolVersion.MSNP18))
        if (verResponse.protocols.size == 1 && verResponse.protocols[0] == ProtocolVersion.UNKNOWN) {
            return AuthenticationResult.UnsupportedProtocol
        }

        val systemInfo = systemInfoRetriever.getSystemInfo()

        val usrResponse = try {
            transport.sendCvr(
                locale = systemInfo.locale,
                osType = systemInfo.osType,
                osVersion = systemInfo.osVersion,
                arch = systemInfo.arch,
                clientName = clientName,
                clientVersion = clientVersion,
                passport = username
            )

            transport.sendUsrSSOInit(username)
        } catch (e: TransportException) {
            return AuthenticationResult.InvalidUser
        }

        val requestBody = multipleSecurityTokensRequestFactory.createRequest(
            username = username,
            password = password
        ).toRequestBody("application/xml".toMediaType())

        val request = Request.Builder()
            .url(Endpoints.RSTUrl)
            .post(requestBody)
            .build()

        val response = try {
            okHttpClient
                .newCall(request)
                .execute()
        } catch (e: IOException) {
            return AuthenticationResult.ServerError
        }

        if (response.isSuccessful.not()) {
            return AuthenticationResult.ServerError
        }
        val xml = response.body!!.string()
        val token = requestSecurityTokenParser.parse(xml) ?: return AuthenticationResult.InvalidPassword
        val decodedToken = ticketEncoder.encode(token.secret, usrResponse.nonce)

        try {
            transport.sendUsrSSOStatus(
                nonce = token.nonce,
                encryptedToken = decodedToken,
                machineGuid = UUID.randomUUID()
            )
        } catch (e: TransportException) {
            return AuthenticationResult.ServerError
        }
        val mspAuthToken = transport.waitForMspAuthToken()
        return AuthenticationResult.Success(username, mspAuthToken)
    }

    suspend fun changeStatus(status: Status) {
        val literalStatus = when (status) {
            Status.ONLINE -> "NLN"
            Status.AWAY -> "AWY"
            Status.BE_RIGHT_BACK -> "BRB"
            Status.IDLE -> "IDL"
            Status.OUT_TO_LUNCH -> "LUN"
            Status.ON_THE_PHONE -> "PHN"
            Status.BUSY -> "BSY"
            Status.OFFLINE -> "FLN"
            Status.HIDDEN -> "HDN"
        }
        val transport = NotificationTransportManager.transport
        try {
            transport.sendChg(NotificationSendCommand.CHG(literalStatus))
        } catch (e: TransportException) {

        }

    }

    suspend fun updatePersonalMessage(text: String) {
        try {
            transport.sendUux(text)
        } catch (e: TransportException) {

        }
    }

    suspend fun contactChanged() = transport.contactChanged()
}


sealed class ChangeStatusResult {

    object Success : ChangeStatusResult()
    object Failure : ChangeStatusResult()

}

sealed class AuthenticationResult {

    object UnsupportedProtocol : AuthenticationResult()
    object InvalidPassword : AuthenticationResult()
    object InvalidUser : AuthenticationResult()
    object ServerError : AuthenticationResult()
    data class Success(val passport: String, val token: String) : AuthenticationResult()

}
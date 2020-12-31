package protocol.notification

import core.SwitchBoardManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import protocol.ProtocolVersion
import protocol.Status
import protocol.utils.Arch
import protocol.utils.LocaleId
import protocol.utils.OSType
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NotificationTransportManager {
    val transport = NotificationTransport()
}

class TransportException(
    val code: Int
) : Exception()

class NotificationTransport {

    private val socket: NotificationSocket = NotificationSocket()
    private val parser = ReceiveCommandParser()
    private val continuations: MutableMap<Int, Continuation<NotificationReceiveCommand>> = mutableMapOf()
    private var continuationMspAuthToken: Continuation<String>? = null
    private var sequence: Int = 1
    private val contactChanged = Channel<ProfileData>()

    fun connect() {
        socket.connect()
        GlobalScope.launch {
            while (true) {
                readNext()
            }
        }
    }

    fun disconnect() {
        socket.close()
    }

    suspend fun sendVer(protocols: List<ProtocolVersion>): NotificationReceiveCommand.VER =
        suspendCoroutine { cont ->
            val protocolsStr = protocols.joinToString(" ") {
                when (it) {
                    ProtocolVersion.MSNP18 -> "MSNP18"
                    ProtocolVersion.UNKNOWN -> ""
                }
            }
            sendMessage("VER $sequence $protocolsStr", cont)
        }

    suspend fun sendCvr(
        locale: LocaleId,
        osType: OSType,
        osVersion: String,
        arch: Arch,
        clientName: String,
        clientVersion: String,
        passport: String
    ): NotificationReceiveCommand.CVR =
        suspendCoroutine { cont ->
            val language = locale.microsoftValue
            val osTypeStr = when (osType) {
                OSType.WINNT -> "win"
                OSType.MACOSX -> "macos"
                OSType.LINUX -> "linux"
            }
            val archStr = when (arch) {
                Arch.I386 -> "i386"
                Arch.AMD64 -> "amd64"
            }
            val message =
                "CVR $sequence $language $osTypeStr $osVersion $archStr $clientName $clientVersion msmgs $passport"
            sendMessage(message, cont)
        }

    suspend fun sendUsrSSOInit(passport: String): NotificationReceiveCommand.USRSSOStatus =
        suspendCoroutine { cont ->
            val message = "USR $sequence SSO I $passport"
            sendMessage(message, cont)
        }

    suspend fun sendUsrSSOStatus(
        nonce: String,
        encryptedToken: String,
        machineGuid: UUID
    ): NotificationReceiveCommand.USRSSOAck =
        suspendCoroutine { cont ->
            val message = "USR $sequence SSO S t=$nonce $encryptedToken {$machineGuid}"
            sendMessage(message, cont)
        }

    suspend fun sendChg(request: NotificationSendCommand.CHG): NotificationReceiveCommand.CHG =
        suspendCoroutine { cont ->
            //TODO set the client's capabilities
            val message = "CHG $sequence ${request.status} 0 0"
            sendMessage(message, cont)
        }

    suspend fun sendXfr(): NotificationReceiveCommand.XFR =
        suspendCoroutine { cont ->
            val message = "XFR $sequence SB"
            sendMessage(message, cont)
        }

    suspend fun sendUux(text: String) : NotificationReceiveCommand.UUX =
        suspendCoroutine { cont ->
            val body = "<Data><PSM>$text</PSM><CurrentMedia></CurrentMedia></Data>"
            val message = "UUX $sequence ${body.length}\r\n$body"
            sendMessage(message, cont)
        }

    suspend fun waitForMspAuthToken(): String =
        suspendCoroutine { cont ->
            continuationMspAuthToken = cont
        }

    fun contactChanged(): Flow<ProfileData> {
        return contactChanged.consumeAsFlow()
    }

    private fun sendMessage(message: String, continuation: Continuation<*>) {
        continuations[sequence] = continuation as Continuation<NotificationReceiveCommand>
        socket.sendMessage(message)
        sequence++
    }

    private suspend fun readNext() {
        val message = socket.readMessage()
        when (val command = parser.parse(message)) {
            is NotificationReceiveCommand.VER -> resumeContinuation(command.sequence, command)
            is NotificationReceiveCommand.USRSSOStatus -> resumeContinuation(command.sequence, command)
            is NotificationReceiveCommand.CVR -> resumeContinuation(command.sequence, command)
            is NotificationReceiveCommand.GCF -> socket.readRaw(command.length)
            is NotificationReceiveCommand.USRSSOAck -> resumeContinuation(command.sequence, command)
            is NotificationReceiveCommand.MSG -> {
                val profileInfo = socket.readRaw(command.length)
                val token = parseProfileInfo(profileInfo)
                token?.let {
                    continuationMspAuthToken?.resume(token)
                    continuationMspAuthToken = null
                }
            }
            is NotificationReceiveCommand.UBX -> {
                if (command.length > 0) {
                    val body = socket.readRaw(command.length)
                    val data = UbxBodyParser().parse(body)
                    val profileData = ProfileData(
                        passport = command.email,
                        personalMessage = data.personalMessage,
                        nickname = null,
                        status = null
                    )
                    contactChanged.offer(profileData)
                }
            }
            is NotificationReceiveCommand.CHG -> resumeContinuation(command.sequence, command)
            is NotificationReceiveCommand.RNG -> SwitchBoardManager.inviteReceived(
                command.sessionId,
                command.address,
                command.port,
                command.passport,
                command.auth
            )
            is NotificationReceiveCommand.XFR -> resumeContinuation(command.sequence, command)
            is NotificationReceiveCommand.NLN -> {
                val profileData = ProfileData(
                    passport = command.passport,
                    status = command.status,
                    nickname = command.displayName,
                    personalMessage = null,
                )
                contactChanged.offer(profileData)
            }
            is NotificationReceiveCommand.FLN -> {
                val profileData = ProfileData(
                    passport = command.passport,
                    status = Status.OFFLINE,
                    nickname = null,
                    personalMessage = null,
                )
                contactChanged.offer(profileData)
            }
            is NotificationReceiveCommand.Error -> resumeErrorContinuation(command.sequence, command)
            is NotificationReceiveCommand.Unknown -> println("NT - Command not supported: $message")
        }
    }

    private fun resumeContinuation(sequence: Int, command: NotificationReceiveCommand) {
        continuations[sequence]!!.resume(command)
        continuations.remove(sequence)
    }

    private fun resumeErrorContinuation(sequence: Int, error: NotificationReceiveCommand.Error) {
        continuations[sequence]!!.resumeWithException(TransportException(error.code))
    }

    private fun parseProfileInfo(content: String): String? {
        val lines = content.split("\n")
        val keyValues = lines.mapNotNull {
            if (it.isBlank()) {
                null
            } else {
                val keyValue = it.split(": ")
                if (keyValue.size == 1) {
                    keyValue[0] to null
                } else {
                    keyValue[0] to keyValue[1].trim()
                }
            }
        }.toMap()
        val contentType = keyValues["Content-Type"].orEmpty()
        if (contentType.contains("text/x-msmsgsprofile")) {
            val profile = ProfileInformation(
                MIMEVersion = keyValues["MIME-Version"].orEmpty(),
                loginTime = keyValues["LoginTime"]?.toLong() ?: -1,
                emailEnabled = keyValues["EmailEnabled"] == "1",
                memberHighId = keyValues["MemberIdHigh"]?.toLong() ?: -1,
                memberLowId = keyValues["MemberIdLow"]?.toLong() ?: -1,
                langPreference = keyValues["lang_preference"]?.toInt() ?: -1,
                preferredEmail = keyValues["preferredEmail"],
                country = keyValues["country"],
                postalCode = keyValues["PostalCode"],
                gender = keyValues["Gender"],
                isKid = keyValues["Kid"] == "1",
                age = keyValues["Age"].orEmpty(),
                birthDayPresent = keyValues["BDayPre"].orEmpty(),
                birthday = keyValues["Birthday"].orEmpty(),
                wallet = keyValues["Wallet"].orEmpty(),
                flags = keyValues["Flags"]?.toInt() ?: -1,
                sid = keyValues["sid"]?.toInt() ?: -1,
                mspAuth = keyValues["MSPAuth"].orEmpty(),
                clientIp = keyValues["ClientIP"].orEmpty(),
                clientPort = keyValues["ClientPort"]?.toInt() ?: -1,
                abchMigrated = keyValues["ABCHMigrated"] == "1",
                mpopEnabled = keyValues["MPOPEnabled"] == "1"
            )
            return profile.mspAuth
        }
        return null
    }

}

data class ProfileInformation(
    val MIMEVersion: String,
    val loginTime: Long,
    val emailEnabled: Boolean,
    val memberHighId: Long,
    val memberLowId: Long,
    val langPreference: Int,
    val preferredEmail: String?,
    val country: String?,
    val postalCode: String?,
    val gender: String?,
    val isKid: Boolean,
    val age: String,
    val birthDayPresent: String,
    val birthday: String,
    val wallet: String,
    val flags: Int,
    val sid: Int,
    val mspAuth: String,
    val clientIp: String,
    val clientPort: Int,
    val abchMigrated: Boolean,
    val mpopEnabled: Boolean
)

data class ProfileData(
    val passport: String,
    val nickname: String?,
    val personalMessage: String?,
    val status: Status?
)
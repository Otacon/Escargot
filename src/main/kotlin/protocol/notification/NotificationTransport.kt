package protocol.notification

import core.TokenHolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import protocol.ProtocolVersion
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NotificationTransportManager {
    val transport = NotificationTransport()
}

class NotificationTransport {

    private val socket: NotificationSocket = NotificationSocket()
    private val parser = ReceiveCommandParser()
    private val continuations: MutableMap<Int, Continuation<NotificationReceiveCommand>> = mutableMapOf()
    private var continuationMsgHotmail: Continuation<Unit>? = null
    private var sequence: Int = 1

    fun connect() {
        socket.connect()
        GlobalScope.launch {
            while (true) {
                readNext()
            }
        }
    }

    suspend fun sendVer(request: NotificationSendCommand.VER): NotificationReceiveCommand.VER = suspendCoroutine { cont ->
        val protocols = request.protocols.joinToString(" ") {
            when (it) {
                ProtocolVersion.MSNP18 -> "MSNP18"
                ProtocolVersion.UNKNOWN -> ""
            }
        }
        sendMessage("VER $sequence $protocols", cont)
    }

    suspend fun sendCvr(request: NotificationSendCommand.CVR): NotificationReceiveCommand.CVR = suspendCoroutine { cont ->
        val message = "CVR $sequence ${request.language} ${request.osType} " +
                "${request.osVersion} ${request.arch} ${request.clientName} " +
                "${request.clientVersion} msmgs ${request.passport}"
        sendMessage(message, cont)
    }

    suspend fun sendUsrSSOInit(request: NotificationSendCommand.USRSSOInit): NotificationReceiveCommand.USRSSOStatus =
        suspendCoroutine { cont ->
            val message = "USR $sequence SSO I ${request.passport}"
            sendMessage(message, cont)
        }

    suspend fun sendUsrSSOStatus(request: NotificationSendCommand.USRSSOStatus): NotificationReceiveCommand.USRSSOAck =
        suspendCoroutine { cont ->
            val message = "USR $sequence SSO S t=${request.nonce} ${request.encryptedToken} {${request.machineGuid}}"
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

    suspend fun waitForMsgHotmail(): Unit =
        suspendCoroutine { cont ->
            continuationMsgHotmail = cont
        }

    private fun sendMessage(message: String, continuation: Continuation<*>) {
        continuations[sequence] = continuation as Continuation<NotificationReceiveCommand>
        socket.sendMessage(message)
        sequence++
    }

    private fun readNext() {
        val message = socket.readMessage()
        when (val command = parser.parse(message)) {
            is NotificationReceiveCommand.VER -> continuations[command.sequence]!!.resume(command)
            is NotificationReceiveCommand.USRSSOStatus -> continuations[command.sequence]!!.resume(command)
            is NotificationReceiveCommand.CVR -> continuations[command.sequence]!!.resume(command)
            is NotificationReceiveCommand.GCF -> socket.readRaw(command.length)
            is NotificationReceiveCommand.USRSSOAck -> continuations[command.sequence]!!.resume(command)
            is NotificationReceiveCommand.MSG -> {
                val profileInfo = socket.readRaw(command.length)
                parseProfileInfo(profileInfo)
            }
            is NotificationReceiveCommand.UBX -> socket.readRaw(command.length)
            is NotificationReceiveCommand.CHG -> continuations[command.sequence]!!.resume(command)
            is NotificationReceiveCommand.RNG -> {
                //TODO add ANS response here to accept a the switchboard invitation.
                println("Received a new chat: $command")
            }
            is NotificationReceiveCommand.XFR -> continuations[command.sequence]!!.resume(command)
            is NotificationReceiveCommand.Unknown -> println("Unknown Command : $message")
        }
    }

    private fun parseProfileInfo(content: String) {
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
        val profile = ProfileInformation(
            MIMEVersion = keyValues["MIME-Version"].orEmpty(),
            contentType = keyValues["Content-Type"].orEmpty(),
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
        println(profile)
        TokenHolder.token = profile.mspAuth
        continuationMsgHotmail?.resume(Unit)
        continuationMsgHotmail = null
    }

}

data class ProfileInformation(
    val MIMEVersion: String,
    val contentType: String,
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
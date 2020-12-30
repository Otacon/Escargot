package protocol.notification

import core.Status
import protocol.ProtocolVersion

sealed class NotificationReceiveCommand {

    data class VER(
        val sequence: Int,
        val protocols: List<ProtocolVersion>
    ) : NotificationReceiveCommand()

    data class USRSSOStatus(
        val sequence: Int,
        val nonce: String
    ) : NotificationReceiveCommand()

    data class USRSSOAck(
        val sequence: Int,
        val email: String,
        val isVerified: Boolean,
        val isKid: Boolean
    ) : NotificationReceiveCommand()

    data class CVR(
        val sequence: Int,
        val minVersion: String,
        val recommendedVersion: String,
        val downloadUrl: String,
        val infoUrl: String
    ) : NotificationReceiveCommand()

    data class GCF(
        val length: Int
    ) : NotificationReceiveCommand()

    data class MSG(
        val email: String,
        val nick: String,
        val length: Int
    ) : NotificationReceiveCommand()

    data class UBX(
        val networkId: Int,
        val email: String,
        val length: Int
    ) : NotificationReceiveCommand()

    data class CHG(
        val sequence: Int,
        val status: String,
        val capabilities: String,
        val msnObj: String
    ) : NotificationReceiveCommand()

    data class RNG(
        val sessionId: String,
        val address: String,
        val port: Int,
        val authType: String,
        val auth: String,
        val passport: String,
        val inviteName: String
    ) : NotificationReceiveCommand()

    data class XFR(
        val sequence: Int,
        val address: String,
        val port: Int,
        val auth: String
    ) : NotificationReceiveCommand()

    data class NLN(
        val status: Status,
        val passport: String,
        val displayName: String,
        val networkId: String,
        val msnObj: String
    ) : NotificationReceiveCommand()

    data class Error(
        val code: Int,
        val sequence: Int
    ) : NotificationReceiveCommand()

    object Unknown : NotificationReceiveCommand()
}
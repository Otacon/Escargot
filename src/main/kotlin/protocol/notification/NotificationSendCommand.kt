package protocol.notification

import protocol.ProtocolVersion

sealed class NotificationSendCommand {

    data class VER(
        val protocols: List<ProtocolVersion>
    ) : NotificationSendCommand()

    data class USRSSOInit(
        val passport: String
    ) : NotificationSendCommand()

    data class USRSSOStatus(
        val nonce: String,
        val encryptedToken: String,
        val machineGuid: String
    ) : NotificationSendCommand()

    data class CHG(
        val status: String
    ) : NotificationSendCommand()

    data class CVR(
        val language: String,
        val osType: String,
        val osVersion: String,
        val arch: String,
        val clientName: String,
        val clientVersion: String,
        val passport: String
    ) : NotificationSendCommand()
}
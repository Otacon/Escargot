package protocol.commands

import protocol.ProtocolVersion

sealed class SendCommand {
    data class VER(val protocols: List<ProtocolVersion>) : SendCommand()
    data class USRSSOInit(val passport: String) : SendCommand()
    data class USRSSOStatus(val nonce: String, val encryptedToken: String, val machineGuid: String) : SendCommand()
    data class CHG(val status: String) : SendCommand()
    data class CVR(
        val language: String,
        val osType: String,
        val osVersion: String,
        val arch: String,
        val clientName: String,
        val clientVersion: String,
        val passport: String
    ) : SendCommand()

    data class CAL(val recipient: String) : SendCommand()
}
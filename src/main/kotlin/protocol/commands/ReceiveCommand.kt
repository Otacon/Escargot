package protocol.commands

import ProtocolVersion

sealed class ReceiveCommand {
    data class VER(
        val sequence: Int,
        val protocols: List<ProtocolVersion>
    ) : ReceiveCommand()

    data class USRSSOStatus(
        val sequence: Int,
        val nonce: String
    ) : ReceiveCommand()

    data class USRSSOAck(
        val sequence: Int,
        val email: String,
        val isVerified: Boolean,
        val isKid: Boolean
    ) : ReceiveCommand()

    data class CVR(
        val sequence: Int,
        val minVersion: String,
        val recommendedVersion: String,
        val downloadUrl: String,
        val infoUrl: String
    ) : ReceiveCommand()

    data class GCF(
        val length: Int
    ) : ReceiveCommand()

    data class MSG(
        val email: String,
        val nick: String,
        val length: Int
    ) : ReceiveCommand()

    data class UBX(
        val networkId: Int,
        val email: String,
        val length: Int
    ) : ReceiveCommand()
}
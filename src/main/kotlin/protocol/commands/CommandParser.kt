package protocol.commands

import protocol.ProtocolVersion

interface CommandParser {
    fun parse(command: String): ReceiveCommand
}

class CommandParserChg : CommandParser {
    private val regex = Regex("""CHG (\d+) ([A-Z]{3}) (\d+) (\d+)""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            ReceiveCommand.CHG(
                sequence = it.groupValues[1].toInt(),
                status = it.groupValues[2],
                capabilities = it.groupValues[3],
                msnObj = it.groupValues[4]
            )
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserCvr : CommandParser {

    private val regex = Regex("""CVR (\d+) (\S+) (\S+) (\S+) (\S+) (\S+)""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            ReceiveCommand.CVR(
                sequence = it.groupValues[1].toInt(),
                minVersion = it.groupValues[2],
                recommendedVersion = it.groupValues[4],
                downloadUrl = it.groupValues[5],
                infoUrl = it.groupValues[6]
            )
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserGcf : CommandParser {

    private val regex = Regex("""GCF (\d+) (\d+)""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            val sequence = it.groupValues[1].toInt()
            val length = it.groupValues[2].toInt()
            ReceiveCommand.GCF(length)
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserMsg : CommandParser {

    private val regex = Regex("""MSG (\S+) (\S+) (\d+)""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            val email = it.groupValues[1]
            val nickname = it.groupValues[2]
            val length = it.groupValues[3].toInt()
            ReceiveCommand.MSG(email, nickname, length)
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserRng : CommandParser {

    private val regex = Regex("""RNG (\S+) (\S+) CKI (\S+) (\S+) (\S+) U messenger.hotmail.com 1""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            ReceiveCommand.RNG(
                sessionId = it.groupValues[1],
                address = it.groupValues[2],
                authType = "CKI",
                ticket = it.groupValues[3],
                passport = it.groupValues[4],
                inviteName = it.groupValues[5]
            )
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserUbx : CommandParser {
    private val regex = Regex("""UBX (\d+):(\S*) (\d+)""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            ReceiveCommand.UBX(
                networkId = it.groupValues[1].toInt(),
                email = it.groupValues[2],
                length = it.groupValues[3].toInt()
            )
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserUserSSOAck : CommandParser {

    private val regex = Regex("""USR (\d+) OK (\S+) (\d) (\d)""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            ReceiveCommand.USRSSOAck(
                sequence = it.groupValues[1].toInt(),
                email = it.groupValues[2],
                isVerified = it.groupValues[3] == "1",
                isKid = it.groupValues[4] == "1"
            )
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserUserSSOStatus : CommandParser {

    private val regex = Regex("""USR (\d+) SSO S MBI_KEY_OLD (\S+)""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            ReceiveCommand.USRSSOStatus(
                sequence = it.groupValues[1].toInt(),
                nonce = it.groupValues[2]
            )
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserVer : CommandParser {
    private val regex = Regex("""VER (\d+) (.*)""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            val protocols = it.groupValues[2].split(" ").map { proto ->
                when (proto) {
                    "MSNP18" -> ProtocolVersion.MSNP18
                    else -> ProtocolVersion.UNKNOWN
                }
            }
            ReceiveCommand.VER(
                sequence = it.groupValues[1].toInt(),
                protocols = protocols
            )
        } ?: ReceiveCommand.Unknown
    }

}

class CommandParserXfr : CommandParser {

    private val regex = Regex("""XFR (\d+) SB (\S+):(\d+) CKI (\S+) U messenger.msn.com 1""")

    override fun parse(command: String): ReceiveCommand {
        return regex.find(command)?.let {
            ReceiveCommand.XFR(
                sequence = it.groupValues[1].toInt(),
                address = it.groupValues[2],
                port = it.groupValues[3].toInt(),
                auth = it.groupValues[4]
            )
        } ?: ReceiveCommand.Unknown
    }

}
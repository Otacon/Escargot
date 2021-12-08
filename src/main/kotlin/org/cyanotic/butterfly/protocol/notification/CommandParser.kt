package org.cyanotic.butterfly.protocol.notification

import org.cyanotic.butterfly.protocol.ProtocolVersion
import java.net.URLDecoder

interface CommandParser {
    fun parse(command: String): NotificationReceiveCommand
}

class CommandParserChg : CommandParser {
    private val regex = Regex("""CHG (\d+) ([A-Z]{3}) (\d+) (\d+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.CHG(
                sequence = it.groupValues[1].toInt(),
                status = it.groupValues[2],
                capabilities = it.groupValues[3],
                msnObj = it.groupValues[4]
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserCvr : CommandParser {

    private val regex = Regex("""CVR (\d+) (\S+) (\S+) (\S+) (\S+) (\S+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.CVR(
                sequence = it.groupValues[1].toInt(),
                minVersion = it.groupValues[2],
                recommendedVersion = it.groupValues[4],
                downloadUrl = it.groupValues[5],
                infoUrl = it.groupValues[6]
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserGcf : CommandParser {

    private val regex = Regex("""GCF (\d+) (\d+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            val sequence = it.groupValues[1].toInt()
            val length = it.groupValues[2].toInt()
            NotificationReceiveCommand.GCF(
                sequence = sequence,
                length = length
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserMsg : CommandParser {

    private val regex = Regex("""MSG (\S+) (\S+) (\d+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            val email = it.groupValues[1]
            val nickname = it.groupValues[2]
            val length = it.groupValues[3].toInt()
            NotificationReceiveCommand.MSG(email, nickname, length)
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserRng : CommandParser {

    private val regex = Regex("""RNG (\S+) (\S+):(\d+) CKI (\S+) (\S+) (\S+) U messenger.hotmail.com 1""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.RNG(
                sessionId = it.groupValues[1],
                address = it.groupValues[2],
                port = it.groupValues[3].toInt(),
                authType = "CKI",
                auth = it.groupValues[4],
                passport = it.groupValues[5],
                inviteName = it.groupValues[6]
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserUbx : CommandParser {
    private val regex = Regex("""UBX (\d+):(\S*) (\d+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.UBX(
                networkId = it.groupValues[1].toInt(),
                email = it.groupValues[2],
                length = it.groupValues[3].toInt()
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserUserSSOAck : CommandParser {

    private val regex = Regex("""USR (\d+) OK (\S+) (\d) (\d)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.USRSSOAck(
                sequence = it.groupValues[1].toInt(),
                email = it.groupValues[2],
                isVerified = it.groupValues[3] == "1",
                isKid = it.groupValues[4] == "1"
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserUserSSOStatus : CommandParser {

    private val regex = Regex("""USR (\d+) SSO S MBI_KEY_OLD (\S+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.USRSSOStatus(
                sequence = it.groupValues[1].toInt(),
                nonce = it.groupValues[2]
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserVer : CommandParser {
    private val regex = Regex("""VER (\d+) (.*)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            val protocols = it.groupValues[2].split(" ").map { proto ->
                when (proto) {
                    "MSNP18" -> ProtocolVersion.MSNP18
                    else -> ProtocolVersion.UNKNOWN
                }
            }
            NotificationReceiveCommand.VER(
                sequence = it.groupValues[1].toInt(),
                protocols = protocols
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserXfr : CommandParser {

    private val regex = Regex("""XFR (\d+) SB (\S+):(\d+) CKI (\S+) U messenger.msn.com 1""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.XFR(
                sequence = it.groupValues[1].toInt(),
                address = it.groupValues[2],
                port = it.groupValues[3].toInt(),
                auth = it.groupValues[4]
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserNln : CommandParser {

    private val regex = Regex("""NLN (\S+) 1:(\S+) (\S+) (\S+) (\S+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.NLN(
                status = it.groupValues[1],
                passport = it.groupValues[2],
                displayName = URLDecoder.decode(it.groupValues[3], "UTF-8"),
                networkId = it.groupValues[4],
                msnObj = URLDecoder.decode(it.groupValues[5], "UTF-8")
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserFln : CommandParser {

    private val regex = Regex("""FLN 1:(\S+) (\S+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.FLN(
                passport = it.groupValues[1],
                networkId = it.groupValues[2]
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserUux : CommandParser {

    private val regex = Regex("""UUX (\d+) (\d+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.UUX(
                sequence = it.groupValues[1].toInt(),
                param = it.groupValues[2].toInt()
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}

class CommandParserAdl : CommandParser {
    private val regex = Regex("""ADL (\d+) OK""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.ADL(
                sequence = it.groupValues[1].toInt()
            )
        } ?: NotificationReceiveCommand.Unknown
    }
}

class CommandParserAdlAccept : CommandParser {
    private val regex = Regex("""ADL 0 (\d+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.ADLAccept(
                length = it.groupValues[1].toInt()
            )
        } ?: NotificationReceiveCommand.Unknown
    }
}

class CommandParserNot : CommandParser {
    private val regex = Regex("""NOT (\d+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.NOT(
                length = it.groupValues[1].toInt()
            )
        } ?: NotificationReceiveCommand.Unknown
    }
}

class CommandParserError : CommandParser {

    private val regex = Regex("""^(\d+) (\d+)""")

    override fun parse(command: String): NotificationReceiveCommand {
        return regex.find(command)?.let {
            NotificationReceiveCommand.Error(
                code = it.groupValues[1].toInt(),
                sequence = it.groupValues[2].toInt(),
            )
        } ?: NotificationReceiveCommand.Unknown
    }

}
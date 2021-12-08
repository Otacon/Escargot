package org.cyanotic.butterfly.protocol.notification

import org.cyanotic.butterfly.protocol.utils.Arch
import org.cyanotic.butterfly.protocol.utils.LocaleId
import org.cyanotic.butterfly.protocol.utils.OSType
import java.util.*

class CommandFactory {

    fun createCvr(
        sequence: Int,
        locale: LocaleId,
        osType: OSType,
        osVersion: String,
        arch: Arch,
        clientName: String,
        clientVersion: String,
        passport: String
    ): String {
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
        return "CVR $sequence $language $osTypeStr $osVersion $archStr $clientName $clientVersion msmgs $passport"
    }

    fun createUsrSsoInit(sequence: Int, passport: String): String {
        return "USR $sequence SSO I $passport"
    }

    fun createUsrSsoStatus(sequence: Int, nonce: String, encryptedToken: String, machineGuid: UUID): String {
        return "USR $sequence SSO S t=$nonce $encryptedToken {$machineGuid}"
    }

    fun createChg(sequence: Int, status: String, capabilities: Long): String {
        return "CHG $sequence $status $capabilities 0"
    }

    fun createXfr(sequence: Int): String {
        return "XFR $sequence SB"
    }

    fun createUux(sequence: Int, psm: String, currentMedia: String): String {
        //TODO use XML encoder
        val body = "<Data><PSM>$psm</PSM><CurrentMedia>$currentMedia</CurrentMedia></Data>"
        val bodyLength = body.toByteArray().size
        return "UUX $sequence ${bodyLength}\r\n$body"
    }

    fun createAdl(
        sequence: Int,
        emailPrefix: String,
        emailDomain: String,
        list: ListType,
        contact: ContactType
    ): String {
        val listType = when (list) {
            ListType.ForwardList -> 1
            ListType.AddList -> 2
            ListType.BlockList -> 4
        }
        val contactType = when (contact) {
            ContactType.Passport -> 1
            ContactType.Phone -> 4
        }
        val body = """<ml><d n="$emailDomain"><c n="$emailPrefix" l="$listType" t="$contactType"/></d></ml>"""
        return "ADL $sequence ${body.length}\r\n$body"
    }

}
package core

import protocol.notification.NotificationSendCommand
import protocol.notification.NotificationTransportManager

object ProfileManager {

    var onStatusChanged: (() -> Unit)? = null
    var onUserInfoChanged: (() -> Unit)? = null

    var passport: String = ""
    var nickname: String = ""
    var token: String = ""
    var status: Status = Status.OFFLINE
    var personalMessage: String = ""

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
        transport.sendChg(NotificationSendCommand.CHG(literalStatus))
        this.status = status
        onStatusChanged?.invoke()
    }

    suspend fun changeNick(nick: String) {

    }

    suspend fun changePersonalMessage(personalMessage: String) {

    }

}

enum class Status {
    ONLINE,
    AWAY,
    BE_RIGHT_BACK,
    IDLE,
    OUT_TO_LUNCH,
    ON_THE_PHONE,
    BUSY,
    OFFLINE,
    HIDDEN
}




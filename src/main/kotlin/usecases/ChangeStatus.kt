package usecases

import protocol.NotificationTransport
import protocol.commands.SendCommand

class ChangeStatus(
    private val transport: NotificationTransport
) {

    suspend operator fun invoke(status: Status): ChangeStatusResult {
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
        transport.sendChg(SendCommand.CHG(literalStatus))
        return ChangeStatusResult.Success
    }
}

sealed class ChangeStatusResult {
    object Success : ChangeStatusResult()
    object Failure : ChangeStatusResult()
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
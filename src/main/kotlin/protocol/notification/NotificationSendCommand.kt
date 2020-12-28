package protocol.notification

sealed class NotificationSendCommand {

    data class CHG(
        val status: String
    ) : NotificationSendCommand()

}
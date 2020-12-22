package usecases

import protocol.NotificationTransport
import protocol.commands.SendCommand

class SendMessage(
    private val transport: NotificationTransport
) {

    suspend operator fun invoke(text: String, recipient: String): SendMessageResult {
        transport.sendCal(SendCommand.CAL(recipient))
        return SendMessageResult.Success
    }
}


sealed class SendMessageResult {
    object Success : SendMessageResult()
    object Failure : SendMessageResult()
}
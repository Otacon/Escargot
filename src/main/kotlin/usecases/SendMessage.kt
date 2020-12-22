package usecases

import kotlinx.coroutines.delay

class SendMessage {

    suspend operator fun invoke(text: String, recipient: String): SendMessageResult {
        delay(1000)

        //TODO hook this shit to the socket
        return SendMessageResult.Success
    }
}


sealed class SendMessageResult {
    object Success : SendMessageResult()
    object Failure : SendMessageResult()
}
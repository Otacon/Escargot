package usecases

import core.SwitchBoard

class SendMessage(
    private val switchBoard: SwitchBoard
) {

    suspend operator fun invoke(text: String, recipient: String): SendMessageResult {
        switchBoard.transfer(recipient)
        return SendMessageResult.Success
    }
}


sealed class SendMessageResult {
    object Success : SendMessageResult()
    object Failure : SendMessageResult()
}
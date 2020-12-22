package usecases

import core.SwitchBoardManager
import protocol.switchboard.SwitchBoardSendCommand

class SendMessage(
    private val switchBoard: SwitchBoardManager
) {

    suspend operator fun invoke(text: String, recipient: String): SendMessageResult {
        val switchBoard = switchBoard.getSwitchBoard(recipient)
        switchBoard.sendMsg(SwitchBoardSendCommand.MSG(text.trimEnd()))
        return SendMessageResult.Success
    }
}


sealed class SendMessageResult {
    object Success : SendMessageResult()
    object Failure : SendMessageResult()
}
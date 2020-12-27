package core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import protocol.switchboard.SwitchBoardSendCommand

object ConversationManager {

    private val conversations = mutableSetOf<Conversation>()

    fun getConversation(recipient: String): Conversation = runBlocking {
        val conversation = conversations.firstOrNull { it.recipient == recipient }
        if (conversation == null) {
            val newConversation = Conversation(recipient)
            newConversation.activate()
            conversations.add(newConversation)
            newConversation
        } else {
            conversation
        }
    }

}

class Conversation(
    val recipient: String,
) {

    val messageHistory = mutableListOf<Message>()
    private val messageQueue = Channel<Message>(Channel.UNLIMITED)

    var conversationChanged: (() -> Unit)? = null

    suspend fun activate() {
        GlobalScope.launch {
            for (message in messageQueue) {
                var switchBoard = SwitchBoardManager.switchBoards[recipient]
                if (switchBoard == null || !switchBoard.isOpen) {
                    SwitchBoardManager.sendInvite(recipient)
                    switchBoard = SwitchBoardManager.switchBoards[recipient]!!
                }
                switchBoard.sendMsg(SwitchBoardSendCommand.MSG(message.content))
                messageHistory.add(message)
                conversationChanged?.invoke()
            }
        }
    }

    suspend fun sendMessage(message: Message) {
        messageQueue.send(message)
    }

}

data class Message(
    val sender: String,
    val content: String
) {
    val timestamp = System.currentTimeMillis()
}
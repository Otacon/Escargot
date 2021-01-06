package features.conversation

import core.ConversationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.orfeo.Conversation
import me.orfeo.Message

class ConversationInteractor(
    private val conversationManager: ConversationManager
) {

    suspend fun getConversation(recipient: String): Conversation {
        return conversationManager.getConversation(recipient)
    }

    suspend fun newMessages(conversationId: Long): Flow<Message> {
        return conversationManager.newConversationMessages(conversationId).map {
            conversationManager.markAsRead(conversationId)
            it
        }
    }

    suspend fun sendMessage(conversationId: Long, message: String) {
        conversationManager.sendMessage(conversationId, message)
    }

}
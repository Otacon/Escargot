package org.cyanotic.butterfly.features.conversation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cyanotic.butterfly.core.ButterflyClient
import org.cyanotic.butterfly.database.entities.Contact
import org.cyanotic.butterfly.database.entities.Conversation
import org.cyanotic.butterfly.database.entities.Message

class ConversationInteractor(
    private val client: ButterflyClient
) {

    suspend fun getConversation(recipient: String): Conversation {
        return client.getConversationManager().getConversation(recipient)
    }

    suspend fun newMessages(conversationId: Long): Flow<Message> {
        val conversationManager = client.getConversationManager()
        return conversationManager.newConversationMessages(conversationId).map {
            conversationManager.markAsRead(conversationId)
            it
        }
    }

    suspend fun sendMessage(conversationId: Long, message: String) {
        client.getConversationManager().sendMessage(conversationId, message)
    }

    suspend fun getContact(recipient: String): Contact? {
        return client.getContactManager().getContact(recipient)
    }

    suspend fun sendNudge(conversationId: Long) {
        client.getConversationManager().sendNudge(conversationId)
    }

    suspend fun sendTyping(conversationId: Long) {
        client.getConversationManager().sendTyping(conversationId)
    }

    suspend fun getAccount(): String {
        return client.getAccountManager().account
    }

}
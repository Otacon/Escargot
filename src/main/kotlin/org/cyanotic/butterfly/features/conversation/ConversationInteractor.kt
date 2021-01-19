package org.cyanotic.butterfly.features.conversation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cyanotic.butterfly.core.ContactManager
import org.cyanotic.butterfly.core.ConversationManager
import org.cyanotic.butterfly.database.entities.Contact
import org.cyanotic.butterfly.database.entities.Conversation
import org.cyanotic.butterfly.database.entities.Message

class ConversationInteractor(
    private val conversationManager: ConversationManager,
    private val contactManager: ContactManager
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

    suspend fun getContact(recipient: String): Contact? {
        return contactManager.getContact(recipient)
    }

    suspend fun sendNudge(conversationId: Long) {
        conversationManager.sendNudge(conversationId)
    }

    suspend fun sendTyping(conversationId: Long) {
        conversationManager.sendTyping(conversationId)
    }

}
package org.cyanotic.butterfly.features.conversation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import org.cyanotic.butterfly.core.ButterflyClient
import org.cyanotic.butterfly.core.Conversation
import org.cyanotic.butterfly.core.ConversationMessage
import org.cyanotic.butterfly.database.entities.ContactEntity

class ConversationInteractor(
    private val client: ButterflyClient
) {

    suspend fun getConversation(recipient: String): Conversation {
        return client.getConversationManager().getConversation(recipient)
    }

    suspend fun newMessages(conversation: Conversation): Flow<ConversationMessage> {
        return conversation.incomingMessages()
    }

    suspend fun sendMessage(conversation: Conversation, message: String) {
        conversation.send(ConversationMessage.Text(client.getAccountManager().account, message))
    }

    suspend fun getContact(recipient: String): ContactEntity? {
        return client.getContactManager().getContact(recipient)
    }

    suspend fun sendNudge(conversation: Conversation) {
        conversation.send(ConversationMessage.Nudge(client.getAccountManager().account))
    }

    suspend fun sendTyping(conversation: Conversation) {
        conversation.send(ConversationMessage.Typing(client.getAccountManager().account))
    }

    suspend fun getAccount(): String {
        return client.getAccountManager().account
    }

}
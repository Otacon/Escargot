package org.cyanotic.butterfly.core

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import org.cyanotic.butterfly.protocol.notification.NotificationTransport

class ConversationManager(
    private val accountManager: AccountManager,
    private val notification: NotificationTransport
) {

    private val conversations = mutableSetOf<Conversation>()
    val allIncomingMessages = Channel<ConversationMessage>(Channel.UNLIMITED)

    fun allIncomingMessages() = allIncomingMessages.consumeAsFlow()

    fun getConversation(recipient: String): Conversation {
        val conversation = conversations.firstOrNull { it.recipient.equals(recipient, true) }
        return if (conversation == null) {
            val newConversation = Conversation(accountManager, notification, this, recipient)
            conversations.add(newConversation)
            newConversation
        } else {
            conversation
        }
    }

    fun closeAll() {
        allIncomingMessages.close()
        conversations.forEach { it.close() }
    }

}


sealed class ConversationMessage {

    data class Nudge(val sender: String) : ConversationMessage()
    data class Typing(val sender: String) : ConversationMessage()
    data class Text(val sender: String, val body: String) : ConversationMessage()

}
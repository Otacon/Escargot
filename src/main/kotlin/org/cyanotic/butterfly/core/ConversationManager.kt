package org.cyanotic.butterfly.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import kotlin.coroutines.CoroutineContext

class ConversationManager(
    private val accountManager: AccountManager,
    private val notification: NotificationTransport
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val conversations = mutableSetOf<Conversation>()
    private val allIncomingMessages = Channel<ConversationMessage>(Channel.UNLIMITED)

    fun allIncomingMessages() = allIncomingMessages.consumeAsFlow()

    fun getConversation(recipient: String): Conversation {
        val conversation = conversations.firstOrNull { it.recipient.equals(recipient, true) }
        return if (conversation == null) {
            val newConversation = Conversation(accountManager, notification, recipient)
            launch { newConversation.incomingMessages().collect { allIncomingMessages.offer(it) } }
            conversations.add(newConversation)
            newConversation
        } else {
            conversation
        }
    }

    fun closeAll() {
        job.cancel()
        allIncomingMessages.close()
        conversations.forEach { it.close() }
    }

}


sealed class ConversationMessage {

    data class Nudge(val sender: String) : ConversationMessage()
    data class Typing(val sender: String) : ConversationMessage()
    data class Text(val sender: String, val body: String) : ConversationMessage()

}
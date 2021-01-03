package features.conversationManager

import features.conversation.ConversationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import protocol.notification.NotificationTransport

object ConversationManager {

    private val conversations: MutableSet<ConversationView> = mutableSetOf()

    fun start(transport: NotificationTransport) {
        GlobalScope.launch {
            transport.switchboardInvites().collect { invite ->
                launch(Dispatchers.JavaFx) {
                    val conversation =
                        conversations.firstOrNull { it.recipient == invite.passport } ?: ConversationView(
                            recipient = invite.passport, onClose = { conversations.remove(it) }
                        )
                    conversation.switchboardInvite(invite)
                    conversations.add(conversation)
                }
            }
        }
    }

    fun openConversation(recipient: String) {
        val conversation = conversations.firstOrNull { it.recipient == recipient } ?: ConversationView(
            recipient = recipient, onClose = { conversations.remove(it) }
        )
        conversations.add(conversation)
    }

}
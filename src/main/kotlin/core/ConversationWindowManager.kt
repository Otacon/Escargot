package core

import features.conversation.ConversationView

object ConversationWindowManager {

    private val conversations = mutableSetOf<ConversationView>()

    fun isWindowOpen(recipient: String): Boolean {
        return conversations.firstOrNull { it.recipient == recipient } != null
    }

    fun onConversationWindowOpened(window: ConversationView) {
        conversations.add(window)
    }

    fun onConversationWindowClosed(window: ConversationView) {
        conversations.remove(window)
    }

}
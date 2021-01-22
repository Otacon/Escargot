package org.cyanotic.butterfly.core

import org.cyanotic.butterfly.features.conversation.ConversationView

class ConversationWindowManager {

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

    fun openConversation(recipient: String) {
        val conversation = conversations.firstOrNull { it.recipient == recipient }
        if(conversation == null){

        }
    }

}
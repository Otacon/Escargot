package org.cyanotic.butterfly.features.conversation

import org.cyanotic.butterfly.core.Conversation

data class ConversationModel(
    val account: String,
    val nickname: String,
    val personalMessage: String,
    val conversation: Conversation? = null,
    val messages: List<ConversationMessageModel>,
    val messageText: String
)
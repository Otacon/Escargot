package org.cyanotic.butterfly.features.conversation

data class ConversationModel(
    val account: String,
    val nickname: String,
    val personalMessage: String,
    val conversationId: Long,
    val messages: List<ConversationMessageModel>,
    val messageText: String,
    val sendEnabled: Boolean,
    val isOtherTyping: Boolean
)
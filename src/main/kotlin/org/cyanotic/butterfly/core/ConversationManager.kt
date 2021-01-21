package org.cyanotic.butterfly.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.cyanotic.butterfly.database.ConversationsTable
import org.cyanotic.butterfly.database.MessagesTable
import org.cyanotic.butterfly.database.entities.Conversation
import org.cyanotic.butterfly.database.entities.Message
import org.cyanotic.butterfly.protocol.switchboard.SwitchBoardSendCommand
import kotlin.coroutines.CoroutineContext

class ConversationManager(
    private val accountManager: AccountManager,
    private val switchboardManager: SwitchboardManager,
    private val conversations: ConversationsTable,
    private val messages: MessagesTable
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        receiveNewMessages()
    }

    suspend fun getConversation(recipient: String): Conversation {
        val account = accountManager.account
        val conversation = conversations.getConversationByRecipient(account, recipient)
        return conversation ?: conversations.createConversation(account, recipient)
    }

    fun newConversationMessages(conversationId: Long): Flow<Message> {
        return messages.newConversationMessages(conversationId)
    }

    suspend fun newMessage(): Flow<Conversation> {
        val account = accountManager.account
        return messages.newOtherMessages(account).map { conversations.getConversationById(it.conversation_id) }
    }

    suspend fun sendMessage(conversationId: Long, message: String) {
        val account = accountManager.account
        val conversation = conversations.getConversationById(conversationId)
        val recipient = conversation.recipient
        messages.addMessage(conversationId, account, System.currentTimeMillis(), message)
        val switchboard = switchboardManager.getSwitchboard(recipient)
        switchboard.sendMsg(SwitchBoardSendCommand.MSG(message))
    }

    private fun receiveNewMessages() = launch {
        val account = accountManager.account
        switchboardManager.messages.consumeAsFlow().collect { message ->
            val recipient = if (message.recipient == account) message.sender else message.recipient
            val conversation = conversations.getConversationByRecipient(
                account = account,
                recipient = recipient
            ) ?: conversations.createConversation(
                account = account,
                other = recipient
            )
            messages.addMessage(conversation.id, message.sender, System.currentTimeMillis(), message.message)
        }
    }

    suspend fun markAsRead(conversationId: Long) {
        conversations.markAsRead(conversationId)
    }

    suspend fun sendNudge(conversationId: Long) {
        val account = accountManager.account
        val conversation = conversations.getConversationById(conversationId)
        val recipient = conversation.recipient
        //TODO support different message types
        // messages.addMessage(conversationId, account, System.currentTimeMillis(), message)
        val switchboard = switchboardManager.getSwitchboard(recipient)
        switchboard.sendMSGDatacast(SwitchBoardSendCommand.MSGDatacast(1))
    }

    suspend fun sendTyping(conversationId: Long) {
        val account = accountManager.account
        val conversation = conversations.getConversationById(conversationId)
        val recipient = conversation.recipient
        val switchboard = switchboardManager.getSwitchboard(recipient)
        switchboard.sendMSGControl(SwitchBoardSendCommand.MSGControl(account))
    }

}
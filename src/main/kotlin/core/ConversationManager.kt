package core

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import database.MSNDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import protocol.switchboard.SwitchBoardSendCommand
import kotlin.coroutines.CoroutineContext

object ConversationManager : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val switchboardManager = SwitchboardManager()
    private val account = MSNDB.db.accountsQueries
    private val conversations = MSNDB.db.conversationQueries
    private val messages = MSNDB.db.messagesQueries


    fun start() {
        switchboardManager.start()
        sendNewMessages()
        receiveNewMessages()
    }

    private fun sendNewMessages() = launch {
        val passport = account.getCurrent().executeAsOne().passport
        messages.getNewMessages().asFlow().mapToList().collect { notSyncedMessages ->
            notSyncedMessages.forEach { message ->
                val conversation = conversations.getById(message.conversation_id).executeAsOne()
                if (message.sender == passport) {
                    val switchboard = switchboardManager.getSwitchboard(conversation.recipient)
                    switchboard.sendMsg(SwitchBoardSendCommand.MSG(message.text))
                    messages.markAsSynced(message.id)
                }
            }
        }
    }

    private fun receiveNewMessages() = launch {
        val passport = account.getCurrent().executeAsOne().passport
        switchboardManager.messages.consumeAsFlow().collect {
            val recipient = if (it.recipient == passport) it.sender else it.recipient
            val conversation = conversations.getByAccountRecipient(passport, recipient).executeAsOneOrNull() ?: run {
                conversations.create(passport, recipient)
                conversations.getByAccountRecipient(passport, recipient).executeAsOne()
            }
            messages.add(conversation.id, it.sender, System.currentTimeMillis(), it.message, false)
        }
    }

}
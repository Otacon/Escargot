package features.conversationManager

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import database.MSNDB
import features.conversation.ConversationView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import protocol.notification.NotificationTransportManager
import protocol.switchboard.SwitchBoardSendCommand
import protocol.switchboard.SwitchBoardTransport

object ConversationManager {
    private val switchboardManager = SwitchboardManager()
    private val passport by lazy { MSNDB.db.accountsQueries.getLastUsed().executeAsOne().passport }

    fun start() {
        switchboardManager.start()
        GlobalScope.launch {
            MSNDB.db.messagesQueries.getNewMessages().asFlow().mapToList().collect { messages ->
                messages.forEach { message ->
                    val conversation = MSNDB.db.conversationQueries.getById(message.conversation_id).executeAsOne()
                    if (!message.synced && message.sender == passport) {
                        val switchboard = switchboardManager.getSwitchboard(conversation.recipient)
                        switchboard.sendMsg(SwitchBoardSendCommand.MSG(message.text))
                        MSNDB.db.messagesQueries.markAsSynced(message.id)
                    }
                }
            }
        }
        GlobalScope.launch {
            switchboardManager.messages.consumeAsFlow().collect {
                val recipient = if (it.recipient == passport) {
                    it.sender
                } else {
                    it.recipient
                }
                var conversation =
                    MSNDB.db.conversationQueries.getByAccountRecipient(passport, recipient).executeAsOneOrNull()
                conversation = if (conversation == null) {
                    MSNDB.db.conversationQueries.create(passport, recipient)
                    MSNDB.db.conversationQueries.getByAccountRecipient(passport, recipient).executeAsOne()
                } else {
                    conversation
                }
                MSNDB.db.messagesQueries.add(conversation.id, it.sender, System.currentTimeMillis(), it.message, false)
            }
        }
    }

}


class SwitchboardManager {

    private val passport by lazy { MSNDB.db.accountsQueries.getLastUsed().executeAsOne().passport }
    val messages = Channel<SwitchboardMessage>(Channel.UNLIMITED)

    private val switchboardActor = GlobalScope.actor<SwitchboardOperation> {
        val switchboards = mutableSetOf<SwitchboardElement>()
        for (msg in channel) {
            when (msg) {
                is SwitchboardOperation.AcceptInvite -> {
                    val switchboard = SwitchBoardTransport().apply {
                        connect(msg.address, msg.port)
                        sendAns(SwitchBoardSendCommand.ANS(passport, msg.auth, msg.sessionId))
                    }
                    GlobalScope.launch {
                        switchboard
                            .messageReceived()
                            .collect { messages.offer(SwitchboardMessage(it.contact, passport, it.text)) }
                    }
                    switchboards.add(SwitchboardElement(msg.passport, switchboard))
                }
                is SwitchboardOperation.GetSwitchboard -> {
                    val existingSwitchboard = switchboards.firstOrNull { it.recipient == msg.recipient }
                    if (existingSwitchboard == null) {
                        val switchboard = SwitchBoardTransport().apply {
                            val switchboardParams = NotificationTransportManager.transport.sendXfr()
                            connect(switchboardParams.address, switchboardParams.port)
                            sendUsr(SwitchBoardSendCommand.USR(passport, switchboardParams.auth))
                            sendCal(SwitchBoardSendCommand.CAL(msg.recipient))
                            waitToJoin()
                        }
                        GlobalScope.launch {
                            switchboard
                                .messageReceived()
                                .collect { messages.offer(SwitchboardMessage(it.contact, passport, it.text)) }
                        }
                        switchboards.add(SwitchboardElement(msg.recipient, switchboard))
                        msg.switchboard.complete(switchboard)
                    } else {
                        msg.switchboard.complete(existingSwitchboard.switchboard)
                    }
                }
                is SwitchboardOperation.CloseSwitchboard -> {
                    switchboards.removeIf { it.switchboard == msg.switchboard }
                }
            }
        }
    }

    fun start() {
        GlobalScope.launch {
            NotificationTransportManager.transport.switchboardInvites().collect {
                val invite = SwitchboardOperation.AcceptInvite(
                    address = it.address,
                    port = it.port,
                    auth = it.auth,
                    sessionId = it.sessionId,
                    passport = it.passport
                )
                switchboardActor.send(invite)
            }
        }
    }

    suspend fun getSwitchboard(recipient: String): SwitchBoardTransport {
        val completion = CompletableDeferred<SwitchBoardTransport>()
        switchboardActor.send(SwitchboardOperation.GetSwitchboard(recipient, completion))
        return completion.await()
    }

}

data class SwitchboardMessage(
    val sender: String,
    val recipient: String,
    val message: String
)

data class SwitchboardElement(
    val recipient: String,
    val switchboard: SwitchBoardTransport
)

private sealed class SwitchboardOperation {
    data class AcceptInvite(
        val address: String,
        val port: Int,
        val auth: String,
        val sessionId: String,
        val passport: String
    ) : SwitchboardOperation()

    data class GetSwitchboard(
        val recipient: String,
        val switchboard: CompletableDeferred<SwitchBoardTransport>
    ) : SwitchboardOperation()

    data class CloseSwitchboard(
        val switchboard: SwitchBoardTransport
    ) : SwitchboardOperation()
}
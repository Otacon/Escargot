package features.conversationManager

import database.MSNDB
import features.conversation.ConversationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.orfeo.Conversation
import me.orfeo.Messages
import protocol.notification.NotificationTransport
import protocol.notification.NotificationTransportManager
import protocol.switchboard.SwitchBoardSendCommand
import protocol.switchboard.SwitchBoardTransport

object ConversationManager {

    private val windows: MutableSet<ConversationView> = mutableSetOf()
    private val switchboards: MutableSet<ConversationSwitchboard> = mutableSetOf()
    private val passport by lazy { MSNDB.db.accountsQueries.getLastUsed().executeAsOne().passport }

    private val actor = GlobalScope.actor<ConversationOperation> {
        for (msg in channel) {
            when (msg) {
                is ConversationOperation.SendMessage -> {
                    val conversationSwitchBoard = switchboards.firstOrNull { it.contact == msg.recipient }
                    val switchboard = if (conversationSwitchBoard == null) {
                        val switchboardParams = NotificationTransportManager.transport.sendXfr()
                        val switchboard = SwitchBoardTransport().apply {
                            connect(switchboardParams.address, switchboardParams.port)
                            sendUsr(SwitchBoardSendCommand.USR(passport,switchboardParams.auth))
                            sendCal(SwitchBoardSendCommand.CAL(msg.recipient))
                            waitToJoin()
                            GlobalScope.launch {
                                messageReceived().collect { messageData ->
                                    val conversationId = MSNDB.db.conversationQueries.getByAccountRecipient(passport, messageData.contact).executeAsOneOrNull()?.id ?: run {
                                        MSNDB.db.conversationQueries.create(passport, messageData.contact)
                                        MSNDB.db.conversationQueries.getByAccountRecipient(passport, messageData.contact).executeAsOne().id
                                    }
                                    MSNDB.db.messagesQueries.add(conversationId, System.currentTimeMillis(), messageData.contact, false)
                                }
                            }
                        }
                        val newConversationSwitchboard = ConversationSwitchboard(msg.recipient, switchboard)
                        switchboards.add(newConversationSwitchboard)
                        newConversationSwitchboard
                    } else {
                        conversationSwitchBoard
                    }
                    switchboard.switchboard.sendMsg(SwitchBoardSendCommand.MSG(msg.message))
                }
                is ConversationOperation.AcceptInvite -> {

                }
            }
        }
    }

    fun start() {
        GlobalScope.launch {
            NotificationTransportManager.transport.switchboardInvites().collect { invite ->
                actor.offer(
                    ConversationOperation.AcceptInvite(
                        sessionId = invite.sessionId,
                        address = invite.address,
                        port = invite.port,
                        passport = invite.passport,
                        auth = invite.auth
                    )
                )
            }
        }
    }

    fun sendMessage(recipient: String, message: String) {
        actor.offer(ConversationOperation.SendMessage(recipient = recipient, message = message))
    }

}

sealed class ConversationOperation {

    data class SendMessage(
        val recipient: String,
        val message: String
    ) : ConversationOperation()

    data class AcceptInvite(
        val sessionId: String,
        val address: String,
        val port: Int,
        val passport: String,
        val auth: String
    ) : ConversationOperation()
}

data class ConversationSwitchboard(
    val contact: String,
    var switchboard: SwitchBoardTransport
)
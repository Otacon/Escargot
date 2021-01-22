package org.cyanotic.butterfly.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import mu.KotlinLogging
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import org.cyanotic.butterfly.protocol.notification.SwitchboardInvite
import org.cyanotic.butterfly.protocol.switchboard.MSGBody
import org.cyanotic.butterfly.protocol.switchboard.SwitchBoardSendCommand
import org.cyanotic.butterfly.protocol.switchboard.SwitchBoardTransport
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger("Conversation")

class Conversation(
    private val accountManager: AccountManager,
    private val notification: NotificationTransport,
    private val conversationManager: ConversationManager,
    val recipient: String
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val actor = GlobalScope.actor<ConversationOperation> {
        var switchboard: SwitchBoardTransport? = null
        for (msg in channel) {
            when (msg) {
                is ConversationOperation.InviteReceived -> {
                    val newSwitchboard = SwitchBoardTransport().apply {
                        connect(msg.address, msg.port)
                        sendAns(SwitchBoardSendCommand.ANS(accountManager.account, msg.auth, msg.sessionId))
                    }
                    switchboard = configureSwitchboard(newSwitchboard)
                }
                is ConversationOperation.SendMessage -> {
                    switchboard = switchboard.checkConnection()
                    switchboard.sendMsg(SwitchBoardSendCommand.MSG(msg.text))
                }
                ConversationOperation.SendNudge -> {
                    switchboard = switchboard.checkConnection()
                    switchboard.sendMSGDatacast(SwitchBoardSendCommand.MSGDatacast(1))
                }
                ConversationOperation.SendTyping -> {
                    switchboard?.sendMSGControl(SwitchBoardSendCommand.MSGControl(accountManager.account))
                }
                ConversationOperation.Close -> {
                    switchboard?.disconnect()
                    incomingMessages.close()
                }

            }
        }
    }

    fun incomingMessages() = incomingMessages.receiveAsFlow()

    private val incomingMessages = Channel<ConversationMessage>(Channel.UNLIMITED)

    fun inviteReceived(invite: SwitchboardInvite) {
        val command = ConversationOperation.InviteReceived(
            sessionId = invite.sessionId,
            address = invite.address,
            port = invite.port,
            passport = invite.passport,
            auth = invite.auth
        )
        actor.offer(command)
    }

    fun send(message: ConversationMessage) {
        when (message) {
            is ConversationMessage.Nudge -> actor.offer(ConversationOperation.SendNudge)
            is ConversationMessage.Typing -> actor.offer(ConversationOperation.SendTyping)
            is ConversationMessage.Text -> actor.offer(ConversationOperation.SendMessage(message.body))
        }
    }

    fun close() {
        actor.offer(ConversationOperation.Close)
    }

    private suspend fun SwitchBoardTransport?.checkConnection(): SwitchBoardTransport {
        return if (this == null) {
            val xfrResponse = notification.sendXfr()
            val newSwitchboard = SwitchBoardTransport().apply {
                connect(xfrResponse.address, xfrResponse.port)
                sendUsr(SwitchBoardSendCommand.USR(accountManager.account, xfrResponse.auth))
                sendCal(SwitchBoardSendCommand.CAL(recipient))
                waitToJoin()
            }
            configureSwitchboard(newSwitchboard)
        } else {
            this
        }
    }

    private fun configureSwitchboard(switchboard: SwitchBoardTransport): SwitchBoardTransport {
        launch {
            switchboard.messageReceived().collect {
                val conversationMessage = when (it) {
                    is MSGBody.Text -> ConversationMessage.Text(it.sender, it.text)
                    is MSGBody.Nudge -> ConversationMessage.Nudge(it.sender)
                    is MSGBody.Typing -> ConversationMessage.Typing(it.sender)
                }
                logger.info { "Notifying allIncomingMessages CR:${conversationManager.allIncomingMessages.isClosedForReceive} CS:${conversationManager.allIncomingMessages.isClosedForSend}" }
                conversationManager.allIncomingMessages.offer(conversationMessage)
                logger.info { "Notifying incomingMessages CR:${incomingMessages.isClosedForReceive} CS:${incomingMessages.isClosedForSend}" }
                incomingMessages.offer(conversationMessage)
            }
        }
        return switchboard
    }

}

private sealed class ConversationOperation {
    data class InviteReceived(
        val sessionId: String,
        val address: String,
        val port: Int,
        val passport: String,
        val auth: String
    ) : ConversationOperation()

    data class SendMessage(
        val text: String
    ) : ConversationOperation()

    object SendNudge : ConversationOperation()
    object SendTyping : ConversationOperation()
    object Close : ConversationOperation()
}
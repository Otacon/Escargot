package org.cyanotic.butterfly.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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

    private var switchboard: SwitchBoardTransport? = null
    private val incomingMessages = Channel<ConversationMessage>(Channel.UNLIMITED)

    suspend fun inviteReceived(invite: SwitchboardInvite) {
        val newSwitchboard = SwitchBoardTransport().apply {
            connect(invite.address, invite.port)
            sendAns(SwitchBoardSendCommand.ANS(accountManager.account, invite.auth, invite.sessionId))
        }
        this.switchboard = configureSwitchboard(newSwitchboard)
    }

    suspend fun send(message: ConversationMessage) {
        val switchboard = this.switchboard.let {
            if (it == null) {
                val xfrResponse = notification.sendXfr()
                val newSwitchboard = SwitchBoardTransport().apply {
                    connect(xfrResponse.address, xfrResponse.port)
                    sendUsr(SwitchBoardSendCommand.USR(accountManager.account, xfrResponse.auth))
                    sendCal(SwitchBoardSendCommand.CAL(recipient))
                    waitToJoin()
                }
                configureSwitchboard(newSwitchboard)
            } else {
                it
            }
        }
        when (message) {
            is ConversationMessage.Nudge -> switchboard.sendMSGDatacast(SwitchBoardSendCommand.MSGDatacast(1))
            is ConversationMessage.Typing -> switchboard.sendMSGControl(SwitchBoardSendCommand.MSGControl(accountManager.account))
            is ConversationMessage.Text -> switchboard.sendMsg(SwitchBoardSendCommand.MSG(message.body))
        }
        this.switchboard = switchboard
    }

    fun incomingMessages() = incomingMessages.receiveAsFlow()

    fun close() {
        switchboard?.disconnect()
        incomingMessages.close()
    }

    private fun configureSwitchboard(switchboard: SwitchBoardTransport): SwitchBoardTransport {
        launch{
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
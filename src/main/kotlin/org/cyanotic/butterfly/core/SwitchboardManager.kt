package org.cyanotic.butterfly.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import org.cyanotic.butterfly.protocol.switchboard.SwitchBoardSendCommand
import org.cyanotic.butterfly.protocol.switchboard.SwitchBoardTransport
import kotlin.coroutines.CoroutineContext

class SwitchboardManager(
    private val accountManager: AccountManager,
    private val notification: NotificationTransport
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        launch {
            notification.switchboardInvites().collect {
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

    val messages = Channel<SwitchboardMessage>(Channel.UNLIMITED)

    private val switchboardActor = GlobalScope.actor<SwitchboardOperation> {
        val switchboards = mutableSetOf<SwitchboardElement>()
        for (msg in channel) {
            val passport = accountManager.account
            when (msg) {
                is SwitchboardOperation.AcceptInvite -> {
                    val switchboard = SwitchBoardTransport().apply {
                        connect(msg.address, msg.port)
                        sendAns(SwitchBoardSendCommand.ANS(passport, msg.auth, msg.sessionId))
                        receiveNewMessages()
                    }
                    switchboards.add(SwitchboardElement(msg.passport, switchboard))
                }
                is SwitchboardOperation.GetSwitchboard -> {
                    val existingSwitchboard = switchboards.firstOrNull { it.recipient == msg.recipient }
                    if (existingSwitchboard == null) {
                        val switchboard = SwitchBoardTransport().apply {
                            val switchboardParams = notification.sendXfr()
                            connect(switchboardParams.address, switchboardParams.port)
                            sendUsr(SwitchBoardSendCommand.USR(passport, switchboardParams.auth))
                            sendCal(SwitchBoardSendCommand.CAL(msg.recipient))
                            waitToJoin()
                            receiveNewMessages()
                        }
                        switchboards.add(SwitchboardElement(msg.recipient, switchboard))
                        msg.switchboard.complete(switchboard)
                    } else {
                        msg.switchboard.complete(existingSwitchboard.switchboard)
                    }
                }
                is SwitchboardOperation.CloseSwitchboard -> {
                    switchboards.firstOrNull { it.switchboard == msg.switchboard }?.let {
                        it.switchboard.disconnect()
                        switchboards.remove(it)
                    }
                }
                is SwitchboardOperation.CloseAll -> {
                    switchboards.forEach { it.switchboard.disconnect() }
                    switchboards.clear()
                    msg.continuation.complete(Unit)
                }
            }
        }
    }

    private fun SwitchBoardTransport.receiveNewMessages() = launch {
        val passport = accountManager.account
        messageReceived().collect { messages.offer(SwitchboardMessage(it.contact, passport, it.text)) }
    }

    suspend fun getSwitchboard(recipient: String): SwitchBoardTransport {
        val completion = CompletableDeferred<SwitchBoardTransport>()
        switchboardActor.send(SwitchboardOperation.GetSwitchboard(recipient, completion))
        return completion.await()
    }

    suspend fun disconnectAll() {
        val completion = CompletableDeferred<Unit>()
        switchboardActor.send(SwitchboardOperation.CloseAll(completion))
        completion.await()
    }

}

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

    data class CloseAll(
        val continuation: CompletableDeferred<Unit>
    ) : SwitchboardOperation()
}

data class SwitchboardMessage(
    val sender: String,
    val recipient: String,
    val message: String
)

private data class SwitchboardElement(
    val recipient: String,
    val switchboard: SwitchBoardTransport
)
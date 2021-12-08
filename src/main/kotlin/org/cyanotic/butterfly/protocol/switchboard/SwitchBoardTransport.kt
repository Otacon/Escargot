package org.cyanotic.butterfly.protocol.switchboard

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.nio.charset.StandardCharsets
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val logger = KotlinLogging.logger("Switchboard")

class SwitchBoardTransport {

    private val socket: SwitchBoardSocket = SwitchBoardSocket()
    private val continuations: MutableMap<Int, Continuation<SwitchBoardReceiveCommand>> = mutableMapOf()
    private val parser = SwitchBoardCommandParser()
    private var sequence: Int = 1
    private var joinContinuation: Continuation<Unit>? = null
    private val messages = Channel<MSGBody>(Channel.UNLIMITED)
    private val socketClosed = Channel<Unit>()

    var isOpen = false

    fun connect(address: String, port: Int) {
        socket.connect(address, port)
        GlobalScope.launch {
            var reading = true
            while (reading) {
                val message = socket.readMessage()
                if (message != null) {
                    processMessage(message)
                } else {
                    logger.warn { "Socket closed." }
                    reading = false
                }
            }
        }
    }

    fun socketClosed() = socketClosed.consumeAsFlow()

    fun messageReceived() = messages.consumeAsFlow()

    fun disconnect() {
        socketClosed.offer(Unit)
        socketClosed.close()
        messages.close()
        socket.close()
    }

    suspend fun sendUsr(command: SwitchBoardSendCommand.USR): SwitchBoardReceiveCommand.Usr =
        suspendCoroutine { cont ->
            val message = "USR $sequence ${command.passport} ${command.auth}"
            sendMessage(message, cont)
        }

    suspend fun sendCal(command: SwitchBoardSendCommand.CAL): SwitchBoardReceiveCommand.Cal =
        suspendCoroutine { cont ->
            val message = "CAL $sequence ${command.passport}"
            sendMessage(message, cont)
        }

    suspend fun sendAns(command: SwitchBoardSendCommand.ANS): SwitchBoardReceiveCommand.Ans =
        suspendCoroutine { cont ->
            val message = "ANS $sequence ${command.passport} ${command.auth} ${command.sessionId}"
            sendMessage(message, cont)
        }

    suspend fun waitToJoin() = suspendCoroutine<Unit> { cont -> joinContinuation = cont }

    suspend fun sendMsg(command: SwitchBoardSendCommand.MSG) {
        val body = "MIME-Version: 1.0\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "X-MMS-IM-Format: FN=MS%20Sans%20Serif; EF=; CO=0; CS=0; PF=0\r\n\r\n" +
                command.message
        val bodyLength = body.toByteArray(StandardCharsets.UTF_8).size
        val message = "MSG $sequence U $bodyLength\r\n$body"
        socket.sendMessage(message, sendNewLine = false)
        sequence++
    }

    suspend fun sendMSGDatacast(command: SwitchBoardSendCommand.MSGDatacast) {
        val body = "MIME-Version: 1.0\r\n" +
                "Content-Type: text/x-msnmsgr-datacast\r\n\r\n" +
                "ID: ${command.id}\r\n"
        val bodyLength = body.toByteArray(StandardCharsets.UTF_8).size
        val message = "MSG $sequence U $bodyLength\r\n$body"
        socket.sendMessage(message, false)
        sequence++
    }

    suspend fun sendMSGControl(command: SwitchBoardSendCommand.MSGControl) {
        val body = "MIME-Version: 1.0\r\n" +
                "Content-Type: text/x-msmsgscontrol\r\n\r\n" +
                "TypingUser: ${command.typingUser}\r\n"
        val bodyLength = body.toByteArray(StandardCharsets.UTF_8).size
        val message = "MSG $sequence U $bodyLength\r\n$body"
        socket.sendMessage(message, false)
        sequence++
    }

    private fun sendMessage(message: String, continuation: Continuation<*>) {
        continuations[sequence] = continuation as Continuation<SwitchBoardReceiveCommand>
        socket.sendMessage(message)
        sequence++
    }

    private fun processMessage(message: String) {
        when (val result = parser.parse(message)) {
            SwitchBoardParseResult.Failed -> logger.warn { "UnknownCommand" }
            is SwitchBoardParseResult.Success -> {
                when (val command = result.command) {
                    is SwitchBoardReceiveCommand.Usr -> continuations[command.sequence]!!.resume(command)
                    is SwitchBoardReceiveCommand.Cal -> continuations[command.sequence]!!.resume(command)
                    is SwitchBoardReceiveCommand.Joi -> {
                        isOpen = true
                        joinContinuation?.resume(Unit)
                        joinContinuation = null
                    }
                    is SwitchBoardReceiveCommand.Msg -> {
                        val body = socket.readRaw(command.length)
                        when (val msg = MsgBodyParser().parse(body)) {
                            is MsgBody.Message -> {
                                messages.offer(MSGBody.Text(command.passport, msg.text))
                            }
                            is MsgBody.Typing -> {
                                messages.offer(MSGBody.Typing(command.passport))
                            }
                            MsgBody.Nudge -> {
                                messages.offer(MSGBody.Nudge(command.passport))
                            }
                            MsgBody.Unknown -> logger.warn { "Unknown message body" }
                        }
                    }
                    is SwitchBoardReceiveCommand.Bye -> {
                        socket.close()
                        joinContinuation = null
                        isOpen = false
                    }
                    is SwitchBoardReceiveCommand.Ans -> {
                        continuations[command.sequence]?.resume(command)
                    }
                    is SwitchBoardReceiveCommand.Iro -> {
                    }
                }
            }
        }
    }
}


sealed class MSGBody {

    data class Text(
        val sender: String,
        val text: String
    ) : MSGBody()

    data class Nudge(
        val sender: String
    ) : MSGBody()

    data class Typing(
        val sender: String
    ) : MSGBody()

}

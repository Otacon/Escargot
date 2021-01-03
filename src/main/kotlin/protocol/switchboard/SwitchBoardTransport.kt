package protocol.switchboard

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SwitchBoardTransport {

    private val socket: SwitchBoardSocket = SwitchBoardSocket()
    private val continuations: MutableMap<Int, Continuation<SwitchBoardReceiveCommand>> = mutableMapOf()
    private val parser = SwitchBoardCommandParser()
    private var sequence: Int = 1
    private var joinContinuation: Continuation<Unit>? = null
    private val messages = Channel<MessageData>(Channel.UNLIMITED)
    private val socketClosed = Channel<Unit>()

    var isOpen = false

    fun connect(address: String, port: Int) {
        socket.connect(address, port)
        GlobalScope.launch {
            while (true) {
                try {
                    readNext()
                } catch (e: Exception) {
                    println("SB: Connection closed. Freeing thread.")
                    socketClosed.offer(Unit)
                    socketClosed.close()
                    messages.close()
                    break
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
        val length = body.length
        val message = "MSG $sequence U $length\r\n$body"
        socket.sendMessage(message, sendNewLine = false)
        sequence++
    }

    private fun sendMessage(message: String, continuation: Continuation<*>) {
        continuations[sequence] = continuation as Continuation<SwitchBoardReceiveCommand>
        socket.sendMessage(message)
        sequence++
    }

    private fun readNext() {
        val message = socket.readMessage()
        when (val result = parser.parse(message)) {
            SwitchBoardParseResult.Failed -> println("UnknownCommand")
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
                                messages.offer(MessageData(command.passport, msg.text))
                            }
                            is MsgBody.Typing -> println("Typing")
                            MsgBody.Unknown -> println("No idea!")
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

data class MessageData(
    val contact: String,
    val text: String
)
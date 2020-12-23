package protocol.switchboard

class MsgBodyParser {
    private val msgRegex =
        Regex("""MIME-Version: (\S+)\r\nContent-Type: text/plain; charset=(\S+)\r\n.*\r\n\r\n(.*)""")
    private val typingRegex =
        Regex("""MIME-Version: (\S+)\r\nContent-Type: text/x-msmsgscontrol\r\nTypingUser: (\S+)\r\n""")

    fun parse(body: String): MsgBody {
        msgRegex.find(body)?.let {
            return MsgBody.Message(it.groupValues[3])
        }
        typingRegex.find(body)?.let {
            return MsgBody.Typing(it.groupValues[2])
        }
        return MsgBody.Unknown
    }
}

sealed class MsgBody {
    data class Message(
        val text: String
    ) : MsgBody()

    data class Typing(
        val passport: String
    ) : MsgBody()

    object Unknown : MsgBody()
}
package protocol.switchboard

sealed class SwitchBoardReceiveCommand {
    data class Usr(
        val sequence: Int,
        val email: String,
        val recipientNick: String
    ) : SwitchBoardReceiveCommand()

    data class Cal(
        val sequence: Int,
        val sessionId: String
    ) : SwitchBoardReceiveCommand()

    data class Joi(
        val passport: String,
        val recipientNick: String,
        val capabilities: String
    ) : SwitchBoardReceiveCommand()

    data class Bye(
        val passport: String
    ) : SwitchBoardReceiveCommand()

    data class Msg(
        val passport: String,
        val nick: String,
        val length: Int
    ) : SwitchBoardReceiveCommand()
}
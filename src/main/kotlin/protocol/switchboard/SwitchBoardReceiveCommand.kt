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

    data class Ans(
        val sequence: Int
    ) : SwitchBoardReceiveCommand()

    data class Iro(
        val sequence: Int,
        val index: Int,
        val rosterCount: Int,
        val passport: String,
        val nickname: String,
        val clientId: String
    ) : SwitchBoardReceiveCommand()
}
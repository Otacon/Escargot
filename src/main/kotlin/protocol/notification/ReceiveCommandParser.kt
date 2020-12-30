package protocol.notification

class ReceiveCommandParser : CommandParser {
    private val parsers = listOf(
        CommandParserVer(),
        CommandParserGcf(),
        CommandParserCvr(),
        CommandParserUserSSOStatus(),
        CommandParserUserSSOAck(),
        CommandParserMsg(),
        CommandParserUbx(),
        CommandParserChg(),
        CommandParserRng(),
        CommandParserXfr(),
        CommandParserNln(),
        CommandParserError()
    )

    override fun parse(command: String): NotificationReceiveCommand {
        parsers.forEach {
            val result = it.parse(command)
            if (result !is NotificationReceiveCommand.Unknown) {
                return result
            }
        }
        return NotificationReceiveCommand.Unknown
    }
}
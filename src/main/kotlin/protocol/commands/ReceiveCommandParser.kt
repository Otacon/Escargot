package protocol.commands

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
        CommandParserXfr()
    )

    override fun parse(command: String): ReceiveCommand {
        parsers.forEach {
            val result = it.parse(command)
            if (result !is ReceiveCommand.Unknown) {
                return result
            }
        }
        return ReceiveCommand.Unknown
    }
}
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
        CommandParserChg()
    )

    override fun parse(command: String): ParseResult {
        for (parser in parsers) {
            val result = parser.parse(command)
            if (result is ParseResult.Success) {
                return result
            }
        }
        return ParseResult.Failed
    }
}
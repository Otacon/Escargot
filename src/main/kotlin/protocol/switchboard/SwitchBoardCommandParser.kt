package protocol.switchboard

class SwitchBoardCommandParser : SwitchBoardParser {
    private val parsers = listOf(
        CommandParserUsr(),
        CommandParserCal(),
        CommandParserJoi()
    )

    override fun parse(command: String): SwitchBoardParseResult {
        for (parser in parsers) {
            val result = parser.parse(command)
            if (result is SwitchBoardParseResult.Success) {
                return result
            }
        }
        return SwitchBoardParseResult.Failed
    }
}

class CommandParserUsr : SwitchBoardParser {

    private val regex = Regex("""USR (\d+) OK (\S+) (\S+)""")

    override fun parse(command: String): SwitchBoardParseResult {
        val match = regex.find(command)
        return match?.let {
            SwitchBoardParseResult.Success(
                SwitchBoardReceiveCommand.Usr(
                    sequence = it.groupValues[1].toInt(),
                    email = it.groupValues[2],
                    recipientNick = it.groupValues[3]
                )
            )
        } ?: SwitchBoardParseResult.Failed
    }

}

class CommandParserCal : SwitchBoardParser {

    private val regex = Regex("""CAL (\d+) RINGING (\S+)""")

    override fun parse(command: String): SwitchBoardParseResult {
        val match = regex.find(command)
        return match?.let {
            SwitchBoardParseResult.Success(
                SwitchBoardReceiveCommand.Cal(
                    sequence = it.groupValues[1].toInt(),
                    sessionId = it.groupValues[2]
                )
            )
        } ?: SwitchBoardParseResult.Failed
    }

}

class CommandParserJoi : SwitchBoardParser {

    private val regex = Regex("""JOI (\S+) (\S+) (\S+)""")

    override fun parse(command: String): SwitchBoardParseResult {
        val match = regex.find(command)
        return match?.let {
            SwitchBoardParseResult.Success(
                SwitchBoardReceiveCommand.Joi(
                    passport = it.groupValues[1],
                    recipientNick = it.groupValues[2],
                    capabilities = it.groupValues[3]
                )
            )
        } ?: SwitchBoardParseResult.Failed
    }

}
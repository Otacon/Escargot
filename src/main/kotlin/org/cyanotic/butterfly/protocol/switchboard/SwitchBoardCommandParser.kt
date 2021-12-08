package org.cyanotic.butterfly.protocol.switchboard

class SwitchBoardCommandParser : SwitchBoardParser {
    private val parsers = listOf(
        CommandParserUsr(),
        CommandParserCal(),
        CommandParserJoi(),
        CommandParserBye(),
        CommandParserMsg(),
        CommandParserIro(),
        CommandParserAns()
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

class CommandParserBye : SwitchBoardParser {

    private val regex = Regex("""BYE (\S+)""")

    override fun parse(command: String): SwitchBoardParseResult {
        val match = regex.find(command)
        return match?.let {
            SwitchBoardParseResult.Success(SwitchBoardReceiveCommand.Bye(passport = it.groupValues[1]))
        } ?: SwitchBoardParseResult.Failed
    }

}

class CommandParserMsg : SwitchBoardParser {

    private val regex = Regex("""MSG (\S+) (\S+) (\d+)""")

    override fun parse(command: String): SwitchBoardParseResult {
        val match = regex.find(command)
        return match?.let {
            SwitchBoardParseResult.Success(
                SwitchBoardReceiveCommand.Msg(
                    passport = it.groupValues[1],
                    nick = it.groupValues[2],
                    length = it.groupValues[3].toInt()
                )
            )
        } ?: SwitchBoardParseResult.Failed
    }

}

class CommandParserIro : SwitchBoardParser {

    private val regex = Regex("""IRO (\d+) (\d+) (\d+) (\S+) (\S+) (\S+)""")

    override fun parse(command: String): SwitchBoardParseResult {
        val match = regex.find(command)
        return match?.let {
            SwitchBoardParseResult.Success(
                SwitchBoardReceiveCommand.Iro(
                    sequence = it.groupValues[1].toInt(),
                    index = it.groupValues[2].toInt(),
                    rosterCount = it.groupValues[3].toInt(),
                    passport = it.groupValues[4],
                    nickname = it.groupValues[5],
                    clientId = it.groupValues[6]
                )
            )
        } ?: SwitchBoardParseResult.Failed
    }
}

class CommandParserAns : SwitchBoardParser {

    private val regex = Regex("""ANS (\d+) OK""")

    override fun parse(command: String): SwitchBoardParseResult {
        val match = regex.find(command)
        return match?.let {
            SwitchBoardParseResult.Success(
                SwitchBoardReceiveCommand.Ans(sequence = it.groupValues[1].toInt())
            )
        } ?: SwitchBoardParseResult.Failed
    }
}
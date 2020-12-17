package protocol.commands

class CommandParserMsg : CommandParser {

    private val regex = Regex("""MSG (\S+) (\S+) (\d+)""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val email = match.groupValues[1]
            val nickname = match.groupValues[2]
            val length = match.groupValues[3].toInt()
            ParseResult.Success(ReceiveCommand.MSG(email, nickname, length))
        } else {
            ParseResult.Failed
        }
    }

}
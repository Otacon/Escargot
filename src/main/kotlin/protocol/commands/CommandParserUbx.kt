package protocol.commands

class CommandParserUbx : CommandParser {
    private val regex = Regex("""UBX (\d+):(\S*) (\d+)""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val groups = match.groupValues
            val networkId = groups[1].toInt()
            val email = groups[2]
            val length = groups[3].toInt()
            ParseResult.Success(ReceiveCommand.UBX(networkId, email, length))
        } else {
            ParseResult.Failed
        }
    }

}
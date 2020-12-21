package protocol.commands

class CommandParserChg : CommandParser {
    private val regex = Regex("""CHG (\d+) ([A-Z]{3}) (\d+) (\d+)""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val groups = match.groupValues
            val sequence = groups[1].toInt()
            val status = groups[2]
            val capabilities = groups[3]
            val msnObj = groups[4]
            ParseResult.Success(ReceiveCommand.CHG(sequence, status, capabilities, msnObj))
        } else {
            ParseResult.Failed
        }
    }

}
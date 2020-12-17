package protocol.commands

class CommandParserGcf : CommandParser {

    private val regex = Regex("""GCF (\d+) (\d+)""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val sequence = match.groupValues[1].toInt()
            val length = match.groupValues[2].toInt()
            ParseResult.Success(ReceiveCommand.GCF(length))
        } else {
            ParseResult.Failed
        }
    }

}
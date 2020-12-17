package protocol.commands

class CommandParserCvr : CommandParser {

    private val regex = Regex("""CVR (\d+) (\S+) (\S+) (\S+) (\S+) (\S+)""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val sequence = match.groupValues[1].toInt()
            val minVersion = match.groupValues[2]
            val recommendedVersion = match.groupValues[4]
            val downloadUrl = match.groupValues[5]
            val infoUrl = match.groupValues[6]
            ParseResult.Success(ReceiveCommand.CVR(sequence, minVersion, recommendedVersion, downloadUrl, infoUrl))
        } else {
            ParseResult.Failed
        }
    }

}
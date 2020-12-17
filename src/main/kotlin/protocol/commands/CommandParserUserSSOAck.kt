package protocol.commands

class CommandParserUserSSOAck : CommandParser {

    private val regex = Regex("""USR (\d+) OK (\S+) (\d) (\d)""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val sequence = match.groupValues[1].toInt()
            val email = match.groupValues[2]
            val isVerified = match.groupValues[3] == "1"
            val isKid = match.groupValues[4] == "1"
            ParseResult.Success(ReceiveCommand.USRSSOAck(sequence, email, isVerified, isKid))
        } else {
            ParseResult.Failed
        }
    }

}
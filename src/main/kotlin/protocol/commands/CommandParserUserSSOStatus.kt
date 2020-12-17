package protocol.commands

class CommandParserUserSSOStatus : CommandParser {

    private val regex = Regex("""USR (\d+) SSO S MBI_KEY_OLD (\S+)""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val sequence = match.groupValues[1].toInt()
            val nonce = match.groupValues[2]
            ParseResult.Success(ReceiveCommand.USRSSOStatus(sequence, nonce))
        } else {
            ParseResult.Failed
        }
    }

}
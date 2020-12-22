package protocol.commands

class CommandParserXfr : CommandParser {

    private val regex = Regex("""XFR (\d+) SB (\S+):(\d+) CKI (\S+) U messenger.msn.com 1""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val sequence = match.groupValues[1].toInt()
            val address = match.groupValues[2]
            val port = match.groupValues[3].toInt()
            val auth = match.groupValues[4]
            ParseResult.Success(
                ReceiveCommand.XFR(
                    sequence = sequence,
                    address = address,
                    port = port,
                    auth = auth
                )
            )
        } else {
            ParseResult.Failed
        }
    }

}
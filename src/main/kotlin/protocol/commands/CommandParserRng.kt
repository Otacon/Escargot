package protocol.commands

class CommandParserRng : CommandParser {

    private val regex = Regex("""RNG (\S+) (\S+) CKI (\S+) (\S+) (\S+) U messenger.hotmail.com 1""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val sessionId = match.groupValues[1]
            val address = match.groupValues[2]
            val ticket = match.groupValues[3]
            val passport = match.groupValues[4]
            val inviteName = match.groupValues[5]
            ParseResult.Success(
                ReceiveCommand.RNG(
                    sessionId = sessionId,
                    address = address,
                    authType = "CKI",
                    ticket = ticket,
                    passport = passport,
                    inviteName = inviteName
                )
            )
        } else {
            ParseResult.Failed
        }
    }

}
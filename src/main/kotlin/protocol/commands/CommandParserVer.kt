package protocol.commands

import protocol.ProtocolVersion

class CommandParserVer : CommandParser {
    private val regex = Regex("""VER (\d+) (.*)""")

    override fun parse(command: String): ParseResult {
        val match = regex.find(command)
        return if (match != null) {
            val groups = match.groupValues
            val sequence = groups[1].toInt()
            val protocols = groups[2].split(" ").map {
                when (it) {
                    "MSNP18" -> ProtocolVersion.MSNP18
                    else -> ProtocolVersion.UNKNOWN
                }
            }
            ParseResult.Success(ReceiveCommand.VER(sequence, protocols))
        } else {
            ParseResult.Failed
        }
    }

}
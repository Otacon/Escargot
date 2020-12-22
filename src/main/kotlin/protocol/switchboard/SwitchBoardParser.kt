package protocol.switchboard

interface SwitchBoardParser {
    fun parse(command: String): SwitchBoardParseResult
}
package protocol.switchboard

sealed class SwitchBoardParseResult {
    object Failed : SwitchBoardParseResult()
    data class Success(val command: SwitchBoardReceiveCommand) : SwitchBoardParseResult()
}
package protocol.commands

sealed class ParseResult {
    object Failed : ParseResult()
    data class Success(val command: ReceiveCommand) : ParseResult()
}
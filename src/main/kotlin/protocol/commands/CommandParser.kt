package protocol.commands

interface CommandParser {
    fun parse(command: String): ParseResult
}
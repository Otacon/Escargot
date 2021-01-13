package org.cyanotic.butterfly.protocol.switchboard

interface SwitchBoardParser {
    fun parse(command: String): SwitchBoardParseResult
}
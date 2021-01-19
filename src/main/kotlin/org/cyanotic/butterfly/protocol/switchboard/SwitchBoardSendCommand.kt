package org.cyanotic.butterfly.protocol.switchboard


sealed class SwitchBoardSendCommand {

    data class USR(val passport: String, val auth: String) : SwitchBoardSendCommand()
    data class CAL(val passport: String) : SwitchBoardSendCommand()
    data class MSG(val message: String) : SwitchBoardSendCommand()
    data class MSGDatacast(val id: Int) : SwitchBoardSendCommand()
    data class MSGControl(val typingUser: String) : SwitchBoardSendCommand()
    data class ANS(val passport: String, val auth: String, val sessionId: String) : SwitchBoardSendCommand()

}
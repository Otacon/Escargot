package core

import core_new.ProfileManager
import protocol.switchboard.SwitchBoardSendCommand
import protocol.switchboard.SwitchBoardTransport

object SwitchBoardManager {
    val switchBoards = mutableMapOf<String, SwitchBoardTransport>()
    val profileManager = ProfileManager

    suspend fun inviteReceived(sessionId: String, address: String, port: Int, passport: String, auth: String) {
        val switchboard = SwitchBoardTransport()
        switchboard.connect(address, port)
        switchboard.sendAns(SwitchBoardSendCommand.ANS(profileManager.passport, auth, sessionId))
        switchBoards[passport] = switchboard
    }

    suspend fun inviteSent(address: String, port: Int, auth: String) {
        val switchboard = SwitchBoardTransport()
        switchboard.connect(address, port)
        switchboard.sendUsr(SwitchBoardSendCommand.USR(profileManager.passport, auth))
        switchboard.sendCal(SwitchBoardSendCommand.CAL("orfeo18@hotmail.it"))
        switchboard.waitToJoin()
        switchBoards["orfeo18@hotmail.it"] = switchboard
    }

}
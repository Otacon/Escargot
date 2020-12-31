package core

import protocol.notification.NotificationTransportManager
import protocol.switchboard.SwitchBoardSendCommand
import protocol.switchboard.SwitchBoardTransport

object SwitchBoardManager {
    val switchBoards = mutableMapOf<String, SwitchBoardTransport>()
    lateinit var myPassport: String

    suspend fun inviteReceived(sessionId: String, address: String, port: Int, passport: String, auth: String) {
        val switchboard = SwitchBoardTransport()
        switchboard.connect(address, port)
        switchboard.sendAns(SwitchBoardSendCommand.ANS(myPassport, auth, sessionId))
        switchBoards[passport] = switchboard
    }

    suspend fun sendInvite(passport: String) {
        val xfr = NotificationTransportManager.transport.sendXfr()
        val switchboard = SwitchBoardTransport()
        switchboard.connect(xfr.address, xfr.port)
        switchboard.sendUsr(SwitchBoardSendCommand.USR(myPassport, xfr.auth))
        switchboard.sendCal(SwitchBoardSendCommand.CAL(passport))
        switchboard.waitToJoin()
        switchBoards[passport] = switchboard
    }

    fun disconnect(){
        switchBoards.forEach { key, value -> value.disconnect() }
    }

}
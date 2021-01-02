package core

import database.MSNDB
import protocol.notification.NotificationTransportManager
import protocol.switchboard.SwitchBoardSendCommand
import protocol.switchboard.SwitchBoardTransport
import repositories.profile.ProfileDataSourceLocal

object SwitchBoardManager {
    val switchBoards = mutableMapOf<String, SwitchBoardTransport>()

    suspend fun inviteReceived(sessionId: String, address: String, port: Int, passport: String, auth: String) {
        val switchboard = SwitchBoardTransport()
        switchboard.connect(address, port)
        val myPassport = ProfileDataSourceLocal(MSNDB.db).getCurrentPassport()
        switchboard.sendAns(SwitchBoardSendCommand.ANS(myPassport, auth, sessionId))
        switchBoards[passport] = switchboard
    }

    suspend fun sendInvite(passport: String) {
        val xfr = NotificationTransportManager.transport.sendXfr()
        val switchboard = SwitchBoardTransport()
        switchboard.connect(xfr.address, xfr.port)
        val myPassport = ProfileDataSourceLocal(MSNDB.db).getCurrentPassport()
        switchboard.sendUsr(SwitchBoardSendCommand.USR(myPassport, xfr.auth))
        switchboard.sendCal(SwitchBoardSendCommand.CAL(passport))
        switchboard.waitToJoin()
        switchBoards[passport] = switchboard
    }

    fun disconnect(){
        switchBoards.forEach { (_, value) -> value.disconnect() }
    }

}
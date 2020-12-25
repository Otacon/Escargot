package core

import protocol.notification.NotificationTransport
import protocol.notification.NotificationTransportManager
import protocol.switchboard.SwitchBoardSendCommand
import protocol.switchboard.SwitchBoardTransport

object SwitchBoardManager {
    private val transport: NotificationTransport = NotificationTransportManager.transport
    private val switchBoards = mutableMapOf<String, SwitchBoardTransport>()
    private val profileManager = ProfileManager

    suspend fun getSwitchBoard(passport: String): SwitchBoardTransport {
        if (switchBoards.contains(passport) && switchBoards[passport]!!.isOpen.not()) {
            switchBoards.remove(passport)
        }

        if (!switchBoards.contains(passport)) {
            val result = transport.sendXfr()
            val switchboard = SwitchBoardTransport()
            switchboard.connect(result.address, result.port)
            switchboard.sendUsr(SwitchBoardSendCommand.USR(profileManager.passport, result.auth))
            switchboard.sendCal(SwitchBoardSendCommand.CAL(passport))
            switchboard.waitToJoin()
            switchBoards[passport] = switchboard
        }
        return switchBoards[passport]!!
    }

}
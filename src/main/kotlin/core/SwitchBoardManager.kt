package core

import kotlinx.coroutines.delay
import protocol.NotificationTransport
import protocol.NotificationTransportManager
import protocol.SwitchBoardTransport
import protocol.switchboard.SwitchBoardSendCommand

object SwitchBoardManager {
    private val transport: NotificationTransport = NotificationTransportManager.transport
    private val switchBoards = mutableMapOf<String, SwitchBoardTransport>()

    suspend fun getSwitchBoard(passport: String): SwitchBoardTransport {
        if (!switchBoards.contains(passport)) {
            val result = transport.sendXfr()
            val switchboard = SwitchBoardTransport()
            switchboard.connect(result.address, result.port)
            switchboard.sendUsr(SwitchBoardSendCommand.USR("orfeo.ciano@gmail.com", result.auth))
            switchboard.sendCal(SwitchBoardSendCommand.CAL(passport))
            delay(5000)
            switchBoards[passport] = switchboard
        }
        return switchBoards[passport]!!
    }

}
package features.conversation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import protocol.notification.NotificationTransportManager
import protocol.notification.SwitchboardInvite
import protocol.switchboard.SwitchBoardSendCommand
import protocol.switchboard.SwitchBoardTransport
import repositories.profile.ProfileDataSourceLocal
import kotlin.coroutines.CoroutineContext

class ConversationPresenter(
    private val view: ConversationContract.View,
    private val recipient: String,
    private val profileDataSourceLocal: ProfileDataSourceLocal
) : ConversationContract.Presenter, CoroutineScope {

    private var switchBoardTransport: SwitchBoardTransport? = null
    private val job = Job()
    private var model =
        ConversationModel(myPassport = "", recipient = "", messages = emptyList(), isOtherTyping = false)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start() {

    }

    override fun onSendMessage(message: String) {
        launch(Dispatchers.IO) {
            val switchboard = switchBoardTransport.let {
                if (it == null) {
                    val response = NotificationTransportManager.transport.sendXfr()
                    SwitchBoardTransport().also { s ->
                        s.connect(response.address, response.port)
                        val passport = profileDataSourceLocal.getCurrentPassport()
                        s.sendUsr(SwitchBoardSendCommand.USR(passport, response.auth))
                        s.sendCal(SwitchBoardSendCommand.CAL(recipient))
                        s.waitToJoin()
                        listenForSwitchboardChanges(s)
                    }
                } else {
                    it
                }
            }
            switchboard.sendMsg(SwitchBoardSendCommand.MSG(message))
            val newMessage = ConversationMessageModel.OwnMessage(System.currentTimeMillis(), message)
            model = model.copy(messages = model.messages + newMessage)
            updateUi()
        }
    }

    override fun onSwitchboardInviteReceived(invite: SwitchboardInvite) {
        launch(Dispatchers.IO) {
            switchBoardTransport = SwitchBoardTransport().also {
                it.connect(invite.address, invite.port)
                val passport = profileDataSourceLocal.getCurrentPassport()
                val command = SwitchBoardSendCommand.ANS(passport, invite.auth, invite.sessionId)
                it.sendAns(command)
                listenForSwitchboardChanges(it)
            }
        }
    }

    private fun listenForSwitchboardChanges(switchboard: SwitchBoardTransport) {
        launch(Dispatchers.IO) {
            switchboard.socketClosed().collect { switchBoardTransport = null }
        }
        launch(Dispatchers.IO) {
            switchboard.messageReceived().collect {
                val newMessage = ConversationMessageModel.OtherMessage(System.currentTimeMillis(), it.contact, it.text)
                playNotification()
                model = model.copy(messages = model.messages + newMessage)
                updateUi()
            }
        }
    }

    private fun updateUi() = launch(Dispatchers.JavaFx) {
        view.setWindowTitle(model.recipient)
        view.setHistory(model.messages)
    }

    private fun playNotification() = launch(Dispatchers.JavaFx) {
        view.playNotification()
    }

}

data class ConversationModel(
    val myPassport: String,
    val recipient: String,
    val messages: List<ConversationMessageModel>,
    val isOtherTyping: Boolean
)

sealed class ConversationMessageModel {
    data class OwnMessage(val timestamp: Long, val message: String) : ConversationMessageModel()
    data class OtherMessage(val timestamp: Long, val nickname: String, val message: String) : ConversationMessageModel()
    data class Error(val text: String) : ConversationMessageModel()
}
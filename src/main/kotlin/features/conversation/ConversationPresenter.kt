package features.conversation

import core.SwitchBoardManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import usecases.SendMessage
import usecases.SendMessageResult
import kotlin.coroutines.CoroutineContext

class ConversationPresenter(
    private val view: ConversationContract.View,
    private val sendMessage: SendMessage,
    private val switchBoard: SwitchBoardManager
) : ConversationContract.Presenter, CoroutineScope {

    private val job = Job()
    private var model = ConversationModel(recipient = "", messages = emptyList(), isOtherTyping = false)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start(recipient: String) {
        model = model.copy(recipient = recipient)
        launch(Dispatchers.IO) {
            switchBoard.getSwitchBoard(recipient).messageListener = ::onMessageReceived
            switchBoard.getSwitchBoard(recipient).typingListener = ::onUserTyping
        }
        updateUi()
    }

    override fun onSendMessage(message: String) {
        view.clearMessageInput()
        launch(Dispatchers.IO) {
            model = when (sendMessage(message, model.recipient)) {
                SendMessageResult.Success -> {
                    val ownMessage = ConversationMessageModel.OwnMessage(System.currentTimeMillis(), message)
                    model.copy(messages = model.messages + ownMessage)
                }
                SendMessageResult.Failure -> {
                    val errorMessage = ConversationMessageModel.Error("Failed delivering the message: $message")
                    model.copy(messages = model.messages + errorMessage)
                }
            }
            updateUi()
        }
    }

    private fun updateUi() = launch(Dispatchers.JavaFx) {
        view.setWindowTitle(model.recipient)
        view.setHistory(model.messages)
    }

    private fun onMessageReceived(text: String) {
        val newHistory = model.messages + ConversationMessageModel.OtherMessage(System.currentTimeMillis(), text)
        model = model.copy(messages = newHistory)
        updateUi()
    }

    private fun onUserTyping() {
        model = model.copy(isOtherTyping = true)
    }


}

data class ConversationModel(
    val recipient: String,
    val messages: List<ConversationMessageModel>,
    val isOtherTyping: Boolean
)

sealed class ConversationMessageModel {
    data class OwnMessage(val timestamp: Long, val message: String) : ConversationMessageModel()
    data class OtherMessage(val timestamp: Long, val message: String) : ConversationMessageModel()
    data class Error(val text: String) : ConversationMessageModel()
}
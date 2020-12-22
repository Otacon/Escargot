package features.conversation

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
    private val sendMessage: SendMessage
) : ConversationContract.Presenter, CoroutineScope {

    private val job = Job()
    private var model = ConversationModel(recipient = "", messages = emptyList())
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start() {
        //getHistory...
        //Do something?!
    }

    override fun onSendMessage(message: String) {
        view.clearMessageInput()
        launch(Dispatchers.IO) {
            model = when (sendMessage(message, "recipient")) {
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


}

data class ConversationModel(
    val recipient: String,
    val messages: List<ConversationMessageModel>
)

sealed class ConversationMessageModel {
    data class OwnMessage(val timestamp: Long, val message: String) : ConversationMessageModel()
    data class OtherMessage(val timestamp: Long, val message: String) : ConversationMessageModel()
    data class Error(val text: String) : ConversationMessageModel()
}
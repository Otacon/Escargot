package features.conversation

import core_new.Conversation
import core_new.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ConversationPresenter(
    private val view: ConversationContract.View,
    private val conversation: Conversation
) : ConversationContract.Presenter, CoroutineScope {

    private val job = Job()
    private var model = ConversationModel(recipient = "", messages = emptyList(), isOtherTyping = false)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start() {
        model = model.copy(recipient = conversation.recipient)
        conversation.conversationChanged = ::onMessageReceived
        updateUi()
    }

    override fun onSendMessage(message: String) {
        view.clearMessageInput()
        launch(Dispatchers.IO) {
            conversation.sendMessage(Message("You", message))
            updateUi()
        }
    }

    private fun updateUi() = launch(Dispatchers.JavaFx) {
        view.setWindowTitle(model.recipient)
        view.setHistory(model.messages)
    }

    private fun onMessageReceived() {
        val newHistory = conversation.messageHistory.map {
            ConversationMessageModel.OwnMessage(
                System.currentTimeMillis(),
                it.content
            )
        }
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
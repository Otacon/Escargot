package features.conversation

import core.Conversation
import core.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ConversationPresenter(
    private val view: ConversationContract.View,
    private val conversation: Conversation,
    private val passport: String
) : ConversationContract.Presenter, CoroutineScope {

    private val job = Job()
    private var model = ConversationModel(recipient = "", messages = emptyList(), isOtherTyping = false)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start() {
        model = model.copy(recipient = conversation.recipient)
        conversation.conversationChanged = ::onMessageReceived
        launch(Dispatchers.IO) {
            val newHistory = conversation.messageHistory.map { it.toModel() }
            model = model.copy(messages = newHistory)
            updateUi()
        }
    }

    override fun onSendMessage(message: String) {
        view.clearMessageInput()
        launch(Dispatchers.IO) {
            conversation.sendMessage(Message(passport, message.trim()))
            updateUi()
        }
    }

    private fun updateUi() = launch(Dispatchers.JavaFx) {
        view.setWindowTitle(model.recipient)
        view.setHistory(model.messages)
    }

    private fun onMessageReceived() {
        val newHistory = conversation.messageHistory.map { it.toModel() }
        model = model.copy(messages = newHistory)
        updateUi()
    }

    private fun Message.toModel(): ConversationMessageModel {
        return if (this.sender == passport) {
            ConversationMessageModel.OwnMessage(
                this.timestamp, this.content
            )
        } else {
            ConversationMessageModel.OtherMessage(
                this.timestamp, model.recipient, this.content
            )
        }
    }


}

data class ConversationModel(
    val recipient: String,
    val messages: List<ConversationMessageModel>,
    val isOtherTyping: Boolean
)

sealed class ConversationMessageModel {
    data class OwnMessage(val timestamp: Long, val message: String) : ConversationMessageModel()
    data class OtherMessage(val timestamp: Long, val nickname: String, val message: String) : ConversationMessageModel()
    data class Error(val text: String) : ConversationMessageModel()
}
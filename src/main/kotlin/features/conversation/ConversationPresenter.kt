package features.conversation

import core.AccountManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ConversationPresenter(
    private val view: ConversationContract.View,
    private val recipient: String,
    private val interactor: ConversationInteractor
) : ConversationContract.Presenter, CoroutineScope {

    private val job = Job()

    private var model =
        ConversationModel(account = "", conversationId = 0, messages = emptyList(), isOtherTyping = false)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start() {
        launch(Dispatchers.IO) {
            val account = AccountManager.getCurrentAccount().passport
            val conversation = interactor.getConversation(recipient)
            model = model.copy(account = account, conversationId = conversation.id)
            interactor.newMessages(conversation.id).collect { msg ->
                val message = if (msg.sender != account) {
                    playNotification()
                    ConversationMessageModel.OtherMessage(msg.timestamp, recipient, msg.text)
                } else {
                    ConversationMessageModel.OwnMessage(System.currentTimeMillis(), msg.text)
                }
                model = model.copy(messages = model.messages + message)
                updateUi()
            }
        }
    }

    override fun onSendMessage(message: String) {
        view.clearMessageInput()
        launch(Dispatchers.IO) {
            interactor.sendMessage(model.conversationId, message.trim())
        }
    }

    override fun onDestroy() {
        job.cancel()
    }

    private fun updateUi() = launch(Dispatchers.JavaFx) {
        view.setWindowTitle("Conversation")
        view.setHistory(model.messages)
    }

    private fun playNotification() = launch(Dispatchers.JavaFx) {
        view.playNotification()
    }

}

data class ConversationModel(
    val account: String,
    val conversationId: Long,
    val messages: List<ConversationMessageModel>,
    val isOtherTyping: Boolean
)

sealed class ConversationMessageModel {
    data class OwnMessage(val timestamp: Long, val message: String) : ConversationMessageModel()
    data class OtherMessage(val timestamp: Long, val nickname: String, val message: String) : ConversationMessageModel()
    data class Error(val text: String) : ConversationMessageModel()
}
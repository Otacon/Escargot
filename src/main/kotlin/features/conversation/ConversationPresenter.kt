package features.conversation

import database.MSNDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ConversationPresenter(
    private val view: ConversationContract.View,
    private val recipient: String
) : ConversationContract.Presenter, CoroutineScope {

    private val job = Job()
    private val repository = ConversationRepository()
    private var model =
        ConversationModel(myPassport = "", recipient = "", messages = emptyList(), isOtherTyping = false)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start() {
        launch(Dispatchers.IO) {
            val myPassport = MSNDB.db.accountsQueries.getCurrent().executeAsOne().passport
            repository.newMessages(recipient).collect { messages ->
                val allMessages = messages.map { msg ->
                    if (recipient != myPassport) {
                        playNotification()
                        ConversationMessageModel.OtherMessage(msg.timestamp, recipient, msg.text)
                    } else {
                        ConversationMessageModel.OwnMessage(System.currentTimeMillis(), msg.text)
                    }
                }
                model = model.copy(messages = allMessages)
                updateUi()
            }
        }
    }

    override fun onSendMessage(message: String) {
        launch(Dispatchers.IO) {
            repository.sendMessage(message, recipient)
        }
    }

    override fun onDestroy() {
        job.cancel()
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
package features.conversation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import database.MSNDB
import features.conversationManager.ConversationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import repositories.profile.ProfileDataSourceLocal
import kotlin.coroutines.CoroutineContext

class ConversationPresenter(
    private val view: ConversationContract.View,
    private val recipient: String
) : ConversationContract.Presenter, CoroutineScope {

    private val job = Job()
    private var model =
        ConversationModel(myPassport = "", recipient = "", messages = emptyList(), isOtherTyping = false)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start() {
        launch(Dispatchers.IO) {
            MSNDB.db.messagesQueries.getNewMessages().asFlow().mapToOne().collect { msg ->
                val newMessage =
                    ConversationMessageModel.OtherMessage(System.currentTimeMillis(), recipient, msg.message)
                playNotification()
                model = model.copy(messages = model.messages + newMessage)
                updateUi()
            }
        }
    }

    override fun onSendMessage(message: String) {
        launch(Dispatchers.IO) {
            ConversationManager.sendMessage(recipient, message)
            val newMessage = ConversationMessageModel.OwnMessage(System.currentTimeMillis(), message)
            model = model.copy(messages = model.messages + newMessage)
            updateUi()
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
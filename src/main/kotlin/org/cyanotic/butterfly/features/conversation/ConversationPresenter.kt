package org.cyanotic.butterfly.features.conversation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.cyanotic.butterfly.core.ConversationMessage
import kotlin.coroutines.CoroutineContext

class ConversationPresenter(
    private val view: ConversationContract.View,
    private val interactor: ConversationInteractor
) : ConversationContract.Presenter, CoroutineScope {

    private val job = Job()

    private var model =
        ConversationModel(
            nickname = "",
            personalMessage = "",
            account = "",
            conversation = null,
            messages = emptyList(),
            messageText = "",
            sendEnabled = false,
            isOtherTyping = false,
        )
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val userTyping = Channel<Unit>(Channel.RENDEZVOUS)

    override fun onCreate(recipient: String) {
        launch(Dispatchers.IO) {
            val account = interactor.getAccount()
            val conversation = interactor.getConversation(recipient)
            val other = interactor.getContact(recipient)
            val nickname = other?.nickname ?: recipient
            val personalMessage = other?.personalMessage ?: ""
            model = model.copy(
                nickname = nickname,
                personalMessage = personalMessage,
                account = account,
                conversation = conversation
            )
            updateUi()
            launch(Dispatchers.IO) {
                interactor.newMessages(conversation).collect { msg ->
                    val message = when (msg) {
                        is ConversationMessage.Nudge -> {
                            if (!msg.sender.equals(account, true)) {
                                playNotification()
                            }
                            ConversationMessageModel.Nudge(msg.sender)
                        }
                        is ConversationMessage.Typing -> {
                            ConversationMessageModel.Message(msg.sender, "Typing...")
                        }
                        is ConversationMessage.Text -> {
                            if (!msg.sender.equals(account, true)) {
                                playNotification()
                            }
                            ConversationMessageModel.Message(msg.sender, msg.body)
                        }
                    }
                    model = model.copy(messages = model.messages + message)
                    updateUi()
                }
            }
            launch(Dispatchers.IO) {
                userTyping.consumeAsFlow()
                    .debounce(5000)
                    .collect { interactor.sendTyping(model.conversation!!) }
            }
        }

    }

    override fun onDestroy() {
        job.cancel()
    }

    override fun onMessageChanged(message: String) {
        val cappedText = message.take(400)
        userTyping.offer(Unit)
        model = model.copy(messageText = cappedText, sendEnabled = cappedText.isNotBlank())
        updateUi()
    }

    override fun onSendClicked() {
        sendMessage()
    }

    override fun onEnterPressed() {
        sendMessage()
    }

    override fun onNudgeClicked() {
        launch(Dispatchers.IO) {
            interactor.sendNudge(model.conversation!!)
        }
    }

    private fun sendMessage() {
        val message = model.messageText
        if (message.isNotBlank()) {
            launch(Dispatchers.IO) {
                interactor.sendMessage(model.conversation!!, message.trim())
            }
        }
        val newMessage = ConversationMessageModel.Message(sender = model.account, message = message.trim())
        model = model.copy(messageText = "", messages = model.messages + newMessage)
        updateUi()
    }

    private fun updateUi() = launch(Dispatchers.JavaFx) {
        view.setWindowTitle("Conversation")
        view.setNickname(model.nickname)
        view.setPersonalMessage(model.personalMessage)
        view.setMessageText(model.messageText)
        view.setHistory(model.messages)
        view.setSendButtonEnabled(model.sendEnabled)
    }

    private fun playNotification() = launch(Dispatchers.JavaFx) {
        view.playNotification()
    }

}

sealed class ConversationMessageModel {
    data class Message(val sender: String, val message: String) : ConversationMessageModel()
    data class Nudge(val sender: String) : ConversationMessageModel()
    data class Error(val text: String) : ConversationMessageModel()
}
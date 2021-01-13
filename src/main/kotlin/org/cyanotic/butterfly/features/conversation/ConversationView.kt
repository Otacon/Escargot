package org.cyanotic.butterfly.features.conversation

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.cyanotic.butterfly.core.ConversationManager
import org.cyanotic.butterfly.core.ConversationWindowManager
import org.cyanotic.butterfly.features.notifications.NotificationManager

class ConversationView(
    val recipient: String
) : ConversationContract.View {

    private val presenter = ConversationPresenter(
        this,
        recipient,
        ConversationInteractor(ConversationManager)
    )

    private lateinit var messageHistory: ListView<String>
    private lateinit var chatInput: TextArea
    private val window: Stage

    init {
        val resource = javaClass.getResource("/Conversation.fxml")
        val root = FXMLLoader.load<Scene>(resource)
        window = Stage()
        window.scene = root
        window.show()
        bindViews(root)
        setupListeners()
        presenter.start()
        ConversationWindowManager.onConversationWindowOpened(this)
        window.setOnCloseRequest {
            ConversationWindowManager.onConversationWindowClosed(this)
            presenter.onDestroy()
        }
    }

    override fun setWindowTitle(title: String) {
        window.title = title
    }

    override fun setHistory(messages: List<ConversationMessageModel>) {
        val messagesStr = messages.map {
            when (it) {
                is ConversationMessageModel.OwnMessage -> "You:\n${it.message}"
                is ConversationMessageModel.OtherMessage -> "${it.nickname}:\n${it.message}"
                is ConversationMessageModel.Error -> "Error:\n${it.text}"
            }
        }
        messageHistory.items.clear()
        messageHistory.items.addAll(messagesStr)
    }

    override fun clearMessageInput() {
        chatInput.text = ""
    }

    override fun playNotification() {
        if (window.isFocused.not()) {
            window.toFront()
            NotificationManager.newMessage()
        }
    }

    private fun bindViews(root: Scene) {
        chatInput = root.lookup("#chatInput") as TextArea
        messageHistory = root.lookup("#history") as ListView<String>
    }

    private fun setupListeners() {
        chatInput.setOnKeyPressed { key ->
            if (key.code == KeyCode.ENTER) {
                presenter.onSendMessage(chatInput.text)
            }
        }
    }
}
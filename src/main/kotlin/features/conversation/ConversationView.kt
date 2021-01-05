package features.conversation

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.stage.Stage
import javafx.util.Duration

class ConversationView(
    val recipient: String,
    val onClose: (() -> Unit)
) : ConversationContract.View {

    private val presenter = ConversationPresenter(
        this,
        recipient
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
        window.setOnCloseRequest {
            onClose()
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
            val file = javaClass.getResource("/message.mp3")
            window.toFront()
            MediaPlayer(Media(file.toString())).apply {
                startTime = Duration.ZERO
                play()
            }
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
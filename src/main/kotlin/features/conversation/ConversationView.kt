package features.conversation

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import usecases.SendMessage

class ConversationView : ConversationContract.View {

    private val presenter = ConversationPresenter(
        this,
        SendMessage()
    )

    private lateinit var messageHistory: ListView<String>
    private lateinit var chatInput: TextArea
    private lateinit var window: Stage

    init {
        val resource = javaClass.getResource("/Conversation.fxml")
        val root = FXMLLoader.load<Scene>(resource)
        window = Stage()
        window.scene = root
        bindViews(root)
        setupListeners()
        window.show()
    }

    override fun setWindowTitle(title: String) {
        window.title = title
    }

    override fun setHistory(messages: List<ConversationMessageModel>) {
        val messagesStr = messages.map {
            when (it) {
                is ConversationMessageModel.OwnMessage -> "You:\n${it.message}"
                is ConversationMessageModel.OtherMessage -> "Other:\n${it.message}"
                is ConversationMessageModel.Error -> "Error:\n${it.text}"
            }
        }
        //TODO find a way to make this trash perform much better without refreshing the whole UI.
        messageHistory.items.clear()
        messageHistory.items.addAll(messagesStr)
    }

    override fun clearMessageInput() {
        chatInput.text = ""
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
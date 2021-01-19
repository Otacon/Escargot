package org.cyanotic.butterfly.features.conversation

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.cyanotic.butterfly.core.ContactManager
import org.cyanotic.butterfly.core.ConversationManager
import org.cyanotic.butterfly.core.ConversationWindowManager
import org.cyanotic.butterfly.features.notifications.NotificationManager

class ConversationView(
    private val window: Stage
) : ConversationContract.View {

    @FXML
    private lateinit var personalMessageLabel: Label

    @FXML
    private lateinit var nicknameLabel: Label

    @FXML
    private lateinit var historyListView: ListView<String>

    @FXML
    private lateinit var typingTextArea: TextArea

    @FXML
    private lateinit var sendButton: Button

    @FXML
    private lateinit var nudgeButton: Button

    private val presenter = ConversationPresenter(
        this,
        ConversationInteractor(ConversationManager, ContactManager)
    )

    lateinit var recipient: String

    fun onCreate(recipient: String){
        this.recipient = recipient
        ConversationWindowManager.onConversationWindowOpened(this)
        setupListeners()
        setupButtons()
        presenter.onCreate(recipient)
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
        historyListView.items.clear()
        historyListView.items.addAll(messagesStr)
        historyListView.scrollTo(messagesStr.size)
    }

    override fun playNotification() {
        if (window.isFocused.not()) {
            window.toFront()
            NotificationManager.newMessage()
        }
    }

    override fun setNickname(nickname: String) {
        nicknameLabel.text = nickname
    }

    override fun setPersonalMessage(personalMessage: String) {
        personalMessageLabel.text = personalMessage
    }

    override fun setMessageText(messageText: String) {
        typingTextArea.text = messageText
        typingTextArea.positionCaret(messageText.length)
    }

    override fun setSendButtonEnabled(sendEnabled: Boolean) {
        sendButton.isDisable = sendEnabled.not()
    }

    private fun setupButtons() {
        val sendIcon = ImageView(Image("/images/send.png"))
        sendIcon.fitWidth = 24.0
        sendIcon.fitHeight = 24.0
        sendButton.graphic = sendIcon
        val nudgeIcon = ImageView(Image("/images/nudge.png"))
        nudgeIcon.fitWidth = 24.0
        nudgeIcon.fitHeight = 24.0
        nudgeButton.graphic = nudgeIcon
    }

    private fun setupListeners() {
        typingTextArea.setOnKeyPressed { key ->
            if (key.code == KeyCode.ENTER) {
                presenter.onEnterPressed()
            }
        }
        typingTextArea.textProperty().addListener { _, old, new ->
            if (old != new) {
                presenter.onMessageChanged(new)
            }
        }
        sendButton.setOnMouseClicked {
            presenter.onSendClicked()
        }
        nudgeButton.setOnMouseClicked {
            presenter.onNudgeClicked()
        }
    }

    companion object {

        fun launch(recipient: String) {
            val window = Stage()
            val controller = ConversationView(window)
            val root = FXMLLoader().apply {
                setController(controller)
                location = ConversationView::class.java.getResource("Conversation.fxml")
            }.load<Scene>()
            window.scene = root
            window.show()
            controller.onCreate(recipient)
        }

    }
}
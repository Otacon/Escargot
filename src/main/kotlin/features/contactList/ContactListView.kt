package features.contactList

import core.SwitchBoardManager
import features.appInstance
import features.conversation.ConversationView
import features.login.LoginView
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import protocol.notification.NotificationTransportManager
import kotlin.system.exitProcess


class ContactListView(
    private val stage: Stage
) : ContactListContract.View {

    @FXML
    private lateinit var menuLogout: MenuItem

    @FXML
    private lateinit var menuExit: MenuItem

    @FXML
    private lateinit var profilePicture: ImageView

    @FXML
    private lateinit var nickname: TextField

    @FXML
    private lateinit var status: TextField

    @FXML
    private lateinit var contactsFilter: TextField

    @FXML
    private lateinit var contactList: ListView<ContactModel>

    private val presenter = ContactListPresenter(this)

    fun onCreate() {
        setupListeners()
        contactList.setCellFactory { ContactListCell() }
        presenter.start()
    }

    private fun setupListeners() {
        contactList.setOnMouseClicked { event ->
            if (event.clickCount == 2) {
                val selectedItem = contactList.selectionModel.selectedItem
                presenter.onContactClick(selectedItem)
            }
        }

        contactsFilter.textProperty().addListener { _, old, new ->
            if (old != new) {
                presenter.onContactFilterChanged(new)
            }
        }

        menuLogout.setOnAction {
            NotificationTransportManager.transport.disconnect()
            SwitchBoardManager.disconnect()
            LoginView.launch(stage)
        }
        menuExit.setOnAction {
            NotificationTransportManager.transport.disconnect()
            SwitchBoardManager.disconnect()
            Platform.exit()
            exitProcess(0)
        }
    }

    override fun setProfilePicture(picture: String) {
        if (picture.isNotBlank()) {
            profilePicture.image = Image(picture)
        }
    }

    override fun setNickname(text: String) {
        nickname.text = text
    }

    override fun setStatus(text: String) {
        status.text = text
    }

    override fun setContacts(contacts: List<ContactModel>) {
        contactList.items.clear()
        contactList.items.addAll(contacts)
    }

    override fun openConversation(passport: String) {
        ConversationView(passport)
    }

    companion object {

        fun launch(stage: Stage) {
            val controller = ContactListView(stage)
            val root = FXMLLoader().apply {
                setController(controller)
                location = javaClass.getResource("/ContactList.fxml")
            }.load<Scene>()
            stage.scene = root
            stage.isResizable = true
            stage.show()
            controller.onCreate()
        }

    }
}
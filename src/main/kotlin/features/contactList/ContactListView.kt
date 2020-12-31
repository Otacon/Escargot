package features.contactList

import core.SwitchBoardManager
import features.conversation.ConversationView
import features.login.LoginView
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
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
    private lateinit var contactList: TreeView<ContactModel>

    private val presenter = ContactListPresenter(this)
    private val contactsRoot = TreeItem<ContactModel>(ContactModel.Root)
    private val contactsOnline = TreeItem<ContactModel>(ContactModel.Category("Available"))
    private val contactsOffline = TreeItem<ContactModel>(ContactModel.Category("Offline"))

    fun onCreate() {
        setupListeners()
        contactList.setCellFactory { ContactListCell() }
        contactList.isShowRoot = false
        contactsRoot.children.add(contactsOnline)
        contactsRoot.children.add(contactsOffline)
        contactsOnline.isExpanded = true
        contactsOffline.isExpanded = true
        contactList.root = contactsRoot
        presenter.start()
    }

    private fun setupListeners() {
        contactList.setOnMouseClicked { event ->
            if (event.clickCount == 2) {
                val selectedItem = contactList.selectionModel.selectedItem
                when (val item = selectedItem.value) {
                    ContactModel.Root,
                    is ContactModel.Category -> {
                    }
                    is ContactModel.Contact -> presenter.onContactClick(item)
                }

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

    override fun setContacts(online: List<ContactModel.Contact>, offline: List<ContactModel.Contact>) {

        contactsOnline.children.clear()
        contactsOnline.children.addAll(online.map { TreeItem(it) })
        contactsOnline.value = ContactModel.Category("Available (${online.size})")

        contactsOffline.children.clear()
        contactsOffline.children.addAll(offline.map { TreeItem(it) })
        contactsOffline.value = ContactModel.Category("Offline (${offline.size})")

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
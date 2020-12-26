package features.contactList

import features.conversation.ConversationView
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage


class ContactListView(
    stage: Stage
) : ContactListContract.View {

    private lateinit var profilePicture: ImageView
    private lateinit var nickname: TextField
    private lateinit var status: TextField
    private lateinit var contactList: ListView<ContactModel>
    private val presenter = ContactListPresenter(this,)

    init {
        val resource = javaClass.getResource("/ContactList.fxml")
        val root = FXMLLoader.load<Scene>(resource)
        stage.scene = root
        stage.isResizable = true
        bindViews(root)
        setupListeners()
        contactList.setCellFactory {
            object : ListCell<ContactModel?>() {
                override fun updateItem(item: ContactModel?, empty: Boolean) {
                    super.updateItem(item, empty)
                    val label = item?.let { "${item.nickname} (${item.passport})" }.orEmpty()
                    text = label
                }
            }
        }
        stage.show()
        presenter.start()
    }

    private fun bindViews(root: Scene) {
        profilePicture = root.lookup("#profile_picture") as ImageView
        nickname = root.lookup("#nickname") as TextField
        status = root.lookup("#status") as TextField
        contactList = root.lookup("#contactList") as ListView<ContactModel>
    }

    private fun setupListeners() {
        contactList.setOnMouseClicked { event ->
            if (event.clickCount == 2) {
                val selectedItem = contactList.selectionModel.selectedItem
                presenter.onContactClick(selectedItem)
            }
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
}
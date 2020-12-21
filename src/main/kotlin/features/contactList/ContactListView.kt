package features.contactList

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import protocol.NotificationTransportManager
import usecases.ChangeStatus

class ContactListView(
    private val stage: Stage
) : ContactListContract.View {

    private lateinit var profilePicture: ImageView
    private lateinit var nickname: TextField
    private lateinit var status: TextField
    private val presenter = ContactListPresenter(this, ChangeStatus(NotificationTransportManager.transport))

    init {
        val resource = javaClass.getResource("/ContactList.fxml")
        val root = FXMLLoader.load<Scene>(resource)
        stage.scene = root
        stage.isResizable = true
        bindViews(root)
        stage.show()
        presenter.start()
    }

    private fun bindViews(root: Scene) {
        profilePicture = root.lookup("#profile_picture") as ImageView
        nickname = root.lookup("#nickname") as TextField
        status = root.lookup("#status") as TextField
    }

    override fun setProfilePicture(picture: String) {
        profilePicture.image = Image(picture)
    }

    override fun setNickname(text: String) {
        nickname.text = text
    }

    override fun setStatus(text: String) {
        status.text = text
    }
}
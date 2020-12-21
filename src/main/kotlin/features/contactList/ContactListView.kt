package features.contactList

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import protocol.NotificationTransportManager
import usecases.ChangeStatus
import usecases.GetContacts

class ContactListView(
    private val stage: Stage
) : ContactListContract.View {

    private lateinit var profilePicture: ImageView
    private lateinit var nickname: TextField
    private lateinit var status: TextField
    private lateinit var contactList: ListView<String>
    private val presenter = ContactListPresenter(
        this,
        ChangeStatus(NotificationTransportManager.transport),
        GetContacts(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build())
    )

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
        contactList = root.lookup("#contactList") as ListView<String>
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

    override fun setContacts(contacts: List<ContactModel>) {
        val elements = contacts.map { it.nickname }
        contactList.items.addAll(elements)
    }
}
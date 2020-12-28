package features.contactList

import core.Status.*
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox


class ContactListCell : ListCell<ContactModel>() {

    private lateinit var status: ImageView
    private lateinit var nick: Label
    private lateinit var personalMessage: Label

    override fun updateItem(item: ContactModel?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null || empty) {
            text = null
            graphic = null
            return
        }

        if (graphic == null) {
            val resource = javaClass.getResource("/ItemContact.fxml")
            val root = FXMLLoader.load<HBox>(resource)

            status = root.lookup("#status") as ImageView
            nick = root.lookup("#nick") as Label
            personalMessage = root.lookup("#personal_message") as Label

            text = null
            graphic = root
        }

        val icon = when (item.status) {
            ONLINE -> "/status-online.png"
            AWAY -> "/status-away.png"
            BE_RIGHT_BACK -> "/status-away.png"
            IDLE -> "/status-away.png"
            OUT_TO_LUNCH -> "/status-away.png"
            ON_THE_PHONE -> "/status-away.png"
            BUSY -> "/status-busy.png"
            OFFLINE -> "/status-offline.png"
            HIDDEN -> "/status-offline.png"
        }

        status.image = Image(javaClass.getResourceAsStream(icon))
        nick.text = item.nickname
        personalMessage.text = item.passport
    }

}
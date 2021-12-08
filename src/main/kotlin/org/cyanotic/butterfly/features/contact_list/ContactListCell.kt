package org.cyanotic.butterfly.features.contact_list

import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.cyanotic.butterfly.protocol.Status.*


class ContactListCell : TreeCell<ContactModel>() {

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

        when (item) {
            ContactModel.Root -> {
                text = null
                graphic = null
            }
            is ContactModel.Category -> {
                text = item.name
            }
            is ContactModel.Contact -> {
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
                    ONLINE -> "/images/status-online.png"
                    AWAY -> "/images/status-away.png"
                    BE_RIGHT_BACK -> "/images/status-away.png"
                    IDLE -> "/images/status-away.png"
                    OUT_TO_LUNCH -> "/images/status-away.png"
                    ON_THE_PHONE -> "/images/status-away.png"
                    BUSY -> "/images/status-busy.png"
                    OFFLINE -> "/images/status-offline.png"
                    HIDDEN -> "/images/status-offline.png"
                }

                status.image = Image(javaClass.getResourceAsStream(icon))
                nick.text = item.nickname
                personalMessage.text = item.personalMessage
                tooltip = Tooltip(item.passport)
            }
        }

    }

}
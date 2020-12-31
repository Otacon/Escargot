package features.contactList

import core.Status

data class ContactListModel(
    val profilePicture: String,
    val nickname: String,
    val status: String,
    val filter: String,
    val onlineContacts: List<ContactModel.Contact>,
    val offlineContacts: List<ContactModel.Contact>
)

sealed class ContactModel {
    object Root : ContactModel()

    data class Category(
        val name: String
    ) : ContactModel()

    data class Contact(
        val nickname: String,
        val passport: String,
        val personalMessage: String,
        val status: Status
    ) : ContactModel()

}

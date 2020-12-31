package features.contactList

import protocol.Status

data class ContactListModel(
    val me: ContactModel.Contact,
    val filter: String,
    val contacts: List<ContactModel.Contact>
)

sealed class ContactModel {
    object Root : ContactModel()

    data class Category(
        val name: String
    ) : ContactModel()

    data class Contact(
        val nickname: String?,
        val passport: String,
        val personalMessage: String,
        val status: Status,
        val profilePicture: String?
    ) : ContactModel()

}

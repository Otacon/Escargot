package features.contactList

import core.Status

data class ContactListModel(
    val profilePicture: String,
    val nickname: String,
    val status: String,
    val filter: String,
    val contacts : List<ContactModel>
)

data class ContactModel(
    val nickname: String,
    val passport: String,
    val personalMessage: String,
    val status: Status
)
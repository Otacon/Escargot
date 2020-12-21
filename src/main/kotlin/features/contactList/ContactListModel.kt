package features.contactList

data class ContactListModel(
    val profilePicture: String,
    val nickname: String,
    val status: String,
    val contacts : List<ContactModel>
)

data class ContactModel(
    val nickname: String,
    val label: String
)
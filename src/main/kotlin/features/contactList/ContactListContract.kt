package features.contactList

interface ContactListContract {

    interface View {

        fun setProfilePicture(picture: String)
        fun setNickname(text: String)
        fun setStatus(text: String)
        fun setContacts(online: List<ContactModel.Contact>, offline : List<ContactModel.Contact>)
        fun openConversation(passport: String)

    }

    interface Presenter {

        fun start()
        fun onContactClick(selectedContact: ContactModel.Contact)
        fun onContactFilterChanged(filter: String)

    }

}
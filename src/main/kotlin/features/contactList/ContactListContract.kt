package features.contactList

interface ContactListContract {

    interface View {

        fun setProfilePicture(picture: String)
        fun setNickname(text: String)
        fun setStatus(text: String)
        fun setContacts(contacts: List<ContactModel>)
        fun openConversation(passport: String)

    }

    interface Presenter {

        fun start()
        fun onContactClick(selectedContact: ContactModel)

    }

}
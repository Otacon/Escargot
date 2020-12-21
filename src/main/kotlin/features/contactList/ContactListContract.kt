package features.contactList

interface ContactListContract {

    interface View {

        fun setProfilePicture(picture: String)
        fun setNickname(text: String)
        fun setStatus(text: String)
        fun setContacts(contacts: List<ContactModel>)

    }

    interface Presenter {

        fun start()

    }

}
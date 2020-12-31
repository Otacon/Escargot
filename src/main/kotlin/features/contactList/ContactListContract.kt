package features.contactList

import core.Status

interface ContactListContract {

    interface View {

        fun setProfilePicture(picture: String)
        fun setNickname(text: String)
        fun setPersonalMessage(text: String)
        fun setContacts(online: List<ContactModel.Contact>, offline : List<ContactModel.Contact>)
        fun openConversation(passport: String)
        fun setStatus(status: Status)

    }

    interface Presenter {

        fun start()
        fun onContactClick(selectedContact: ContactModel.Contact)
        fun onContactFilterChanged(filter: String)
        fun onStatusChanged(status: Status)

    }

}
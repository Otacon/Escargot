package org.cyanotic.butterfly.features.contact_list

import org.cyanotic.butterfly.protocol.Status

interface ContactListContract {

    interface View {

        fun setProfilePicture(picture: String?)
        fun setNickname(text: String)
        fun setPersonalMessage(text: String)
        fun setContacts(online: List<ContactModel.Contact>, offline: List<ContactModel.Contact>)
        fun openConversation(recipient: String)
        fun setStatus(status: Status)

    }

    interface Presenter {

        fun start()
        fun onContactClick(selectedContact: ContactModel.Contact)
        fun onContactFilterChanged(filter: String)
        fun onStatusChanged(status: Status)
        fun onPersonalMessageChanged(text: String)
        fun onCancelPersonalMessage()

    }

}
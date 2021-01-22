package org.cyanotic.butterfly.features.contact_list

import org.cyanotic.butterfly.features.add_contact.AddContactResult
import org.cyanotic.butterfly.features.friend_request.FriendRequestResult
import org.cyanotic.butterfly.protocol.Status

interface ContactListContract {

    interface View {

        fun setProfilePicture(picture: String?)
        fun setNickname(text: String)
        fun setPersonalMessage(text: String)
        fun setContacts(online: List<ContactModel.Contact>, offline: List<ContactModel.Contact>)
        fun openConversation(recipient: String)
        fun setStatus(status: Status)
        fun openContactRequest(passport: String)
        fun openLogin()
        fun exit()

    }

    interface Presenter {

        fun onCreate()
        fun onContactClick(selectedContact: ContactModel.Contact)
        fun onContactFilterChanged(filter: String)
        fun onStatusChanged(status: Status)
        fun onPersonalMessageChanged(text: String)
        fun onCancelPersonalMessage()
        fun onAddContactClosed(result: AddContactResult)
        fun onContactRequestResult(result: FriendRequestResult)
        fun onLogoutClicked()
        fun onExitClicked()

    }

}
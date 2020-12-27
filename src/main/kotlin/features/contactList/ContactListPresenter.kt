package features.contactList

import core.ContactManager
import core.ProfileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ContactListPresenter(
    private val view: ContactListContract.View,
) : ContactListContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    var model = ContactListModel(profilePicture = "", nickname = "", status = "", contacts = emptyList())

    override fun start() {
        ContactManager.onContactListChanged = {
            val contacts = ContactManager.contacts.map { ContactModel(it.nickname, it.passport) }
            model = model.copy(contacts = contacts)
            updateUI()
        }
        ProfileManager.onUserInfoChanged = {
            val nickname = ProfileManager.nickname
            model = model.copy(nickname = nickname)
            updateUI()
        }
        launch(Dispatchers.IO) {
            ContactManager.refreshContactList()
        }
    }

    override fun onContactClick(selectedContact: ContactModel) {
        view.openConversation(selectedContact.passport)
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setProfilePicture(model.profilePicture)
        view.setNickname(model.nickname)
        view.setStatus(model.status)
        view.setContacts(model.contacts)
    }


}
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

    var model = ContactListModel(profilePicture = "", nickname = "", status = "", filter = "", contacts = emptyList())

    override fun start() {
        ContactManager.onContactListChanged = {
            val contacts = ContactManager.contacts.map { ContactModel(it.nickname, it.passport, it.status) }
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

    override fun onContactFilterChanged(filter: String) {
        model = model.copy(filter = filter)
        updateUI()
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setProfilePicture(model.profilePicture)
        view.setNickname(model.nickname)
        view.setStatus(model.status)
        view.setContacts(model.contacts.filter {
            "${it.nickname} ${it.passport}".contains(
                model.filter,
                ignoreCase = true
            )
        })
    }


}
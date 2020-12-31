package features.contactList

import core.ContactManager
import core.ProfileManager
import core.Status
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

    var model = ContactListModel(
        profilePicture = "",
        status = Status.ONLINE,
        nickname = "",
        personalMessage = "",
        filter = "",
        onlineContacts = emptyList(),
        offlineContacts = emptyList()
    )

    override fun start() {
        ContactManager.onContactListChanged = {
            val (offline, online) = ContactManager.contacts.map {
                ContactModel.Contact(
                    it.nickname,
                    it.passport,
                    it.personalMessage,
                    it.status
                )
            }
                .partition { it.status == Status.OFFLINE }
            model = model.copy(onlineContacts = online, offlineContacts = offline)
            updateUI()
        }
        ProfileManager.onUserInfoChanged = {
            val nickname = ProfileManager.nickname
            model = model.copy(nickname = nickname)
            updateUI()
        }
        launch(Dispatchers.IO) {
            ProfileManager.changeStatus(model.status)
            ContactManager.refreshContactList()
        }
    }

    override fun onContactClick(selectedContact: ContactModel.Contact) {
        if (selectedContact.status != Status.OFFLINE) {
            view.openConversation(selectedContact.passport)
        }
    }

    override fun onContactFilterChanged(filter: String) {
        model = model.copy(filter = filter)
        updateUI()
    }

    override fun onStatusChanged(status: Status) {
        model = model.copy(status = status)
        launch(Dispatchers.IO) {
            ProfileManager.changeStatus(status)
        }
        updateUI()
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setProfilePicture(model.profilePicture)
        view.setNickname(model.nickname)
        view.setPersonalMessage(model.personalMessage)
        val online = model.onlineContacts.filter {
            "${it.nickname} ${it.passport}".contains(model.filter, ignoreCase = true)
        }.sortedWith(compareBy({ it.status }, { it.nickname }, { it.passport }))
        val offline = model.offlineContacts.filter {
            "${it.nickname} ${it.passport}".contains(model.filter, ignoreCase = true)
        }.sortedWith(compareBy({ it.status }, { it.nickname }, { it.passport }))
        view.setContacts(online, offline)
        view.setStatus(model.status)
    }


}
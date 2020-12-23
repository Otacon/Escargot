package features.contactList

import core.TokenHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import usecases.ChangeStatus
import usecases.GetContacts
import usecases.GetContactsResult
import usecases.Status
import kotlin.coroutines.CoroutineContext

class ContactListPresenter(
    private val view: ContactListContract.View,
    private val changeStatus: ChangeStatus,
    private val getContacts: GetContacts
) : ContactListContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    var model = ContactListModel(profilePicture = "", nickname = "", status = "", contacts = emptyList())

    override fun start() {
        launch(Dispatchers.IO) {
            delay(3000)
            changeStatus(Status.ONLINE)
            model = when (val contactResponse = getContacts(TokenHolder.token)) {
                is GetContactsResult.Success -> {
                    val me = contactResponse.contacts.firstOrNull { it.contactType == "Me" }
                    val contacts = contactResponse.contacts
                        .filter { it.contactType == "Regular" }
                        .map { ContactModel(it.nickname, it.email) }
                    model.copy(contacts = contacts, nickname = me?.nickname ?: "")
                }
                GetContactsResult.Failure -> {
                    val contacts = emptyList<ContactModel>()
                    model.copy(contacts = contacts)
                }
            }
            updateUI()
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
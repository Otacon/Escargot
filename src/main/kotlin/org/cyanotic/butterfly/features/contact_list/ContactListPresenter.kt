package org.cyanotic.butterfly.features.contact_list

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.cyanotic.butterfly.features.add_contact.AddContactResult
import org.cyanotic.butterfly.features.friend_request.FriendRequestResult
import org.cyanotic.butterfly.features.notifications.NotificationManager
import org.cyanotic.butterfly.protocol.Status
import org.cyanotic.butterfly.protocol.asStatus
import kotlin.coroutines.CoroutineContext

class ContactListPresenter(
    private val view: ContactListContract.View,
    private val interactor: ContactListInteractor
) : ContactListContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    var model = ContactListModel(
        nickname = null,
        passport = "",
        personalMessage = "",
        status = Status.ONLINE,
        profilePicture = null,
        filter = "",
        contacts = emptyList()
    )

    override fun start() {
        launch(Dispatchers.IO) {
            interactor.otherContactsUpdates().collect { contacts ->
                val models = contacts.map { contact ->
                    ContactModel.Contact(
                        nickname = contact.nickname,
                        passport = contact.passport,
                        personalMessage = contact.personalMessage ?: "",
                        status = contact.status?.asStatus() ?: Status.OFFLINE
                    )
                }
                model = model.copy(contacts = models)
                launch(Dispatchers.JavaFx) {
                    val notificationEnabled = when (model.status) {
                        Status.ONLINE,
                        Status.AWAY,
                        Status.BE_RIGHT_BACK,
                        Status.IDLE,
                        Status.OUT_TO_LUNCH,
                        Status.OFFLINE,
                        Status.HIDDEN -> true
                        Status.ON_THE_PHONE,
                        Status.BUSY -> false
                    }
                    NotificationManager.notificationsEnabled = notificationEnabled
                    updateUI()
                }
            }
        }
        launch(Dispatchers.IO) {
            interactor.ownContactUpdates().collect { contact ->
                model = model.copy(
                    nickname = contact.nickname,
                    personalMessage = contact.personalMessage,
                    status = contact.status
                )
                launch(Dispatchers.JavaFx) {
                    updateUI()
                }
            }
        }
        launch(Dispatchers.IO) {
            interactor.newMessagesForConversation().collect { conversation ->
                launch(Dispatchers.JavaFx) {
                    view.openConversation(conversation.recipient)
                }
            }
        }
        launch(Dispatchers.IO) {
            interactor.newContactRequests().collect { contact ->
                println("Presenter: Contact Request received: $contact")
                launch(Dispatchers.JavaFx) {
                    view.openContactRequest(contact.passport)
                }
            }
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
        launch(Dispatchers.IO) { interactor.changeStatus(status) }
        updateUI()
    }

    override fun onPersonalMessageChanged(text: String) {
        model = model.copy(personalMessage = text)
        launch(Dispatchers.IO) { interactor.updatePersonalMessage(text) }
    }

    override fun onCancelPersonalMessage() {
        view.setPersonalMessage(model.personalMessage)
    }

    override fun onAddContactClosed(result: AddContactResult) {
        launch(Dispatchers.IO) {
            if (result == AddContactResult.Added) {
                interactor.refreshContactList()
            }
        }
    }

    override fun onContactRequestResult(result: FriendRequestResult) {
        launch(Dispatchers.IO) {
            if (result == FriendRequestResult.Accepted) {
                interactor.refreshContactList()
            }
        }
    }

    override fun onLogoutClicked() {
        launch(Dispatchers.IO){
            interactor.disconnect()
        }
        view.openLogin()
    }

    override fun onExitClicked() {
        launch(Dispatchers.IO){
            interactor.disconnect()
        }
        view.exit()
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setStatus(model.status)
        view.setProfilePicture(model.profilePicture)
        view.setNickname(model.nickname ?: model.passport)
        view.setPersonalMessage(model.personalMessage)

        val (offlineContacts, onlineContacts) = model.contacts.partition { it.status == Status.OFFLINE }
        val online = onlineContacts.filter {
            "${it.nickname} ${it.passport}".contains(model.filter, ignoreCase = true)
        }.sortedWith(compareBy({ it.status }, { it.nickname }, { it.passport }))

        val offline = offlineContacts.filter {
            "${it.nickname} ${it.passport}".contains(model.filter, ignoreCase = true)
        }.sortedWith(compareBy({ it.status }, { it.nickname }, { it.passport }))

        view.setContacts(online, offline)
    }
}
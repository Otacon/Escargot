package features.contactList

import core.ConversationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import protocol.Status
import repositories.contactList.ContactListRepository
import repositories.profile.ProfileRepository
import kotlin.coroutines.CoroutineContext

class ContactListPresenter(
    private val view: ContactListContract.View,
    private val profileRepository: ProfileRepository,
    private val contactListRepository: ContactListRepository
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
            contactListRepository.startListeningForAccountChanges()
        }
        launch(Dispatchers.IO) {
            contactListRepository.contactUpdates().collect { contacts ->
                val models = contacts.map { contact ->
                    ContactModel.Contact(
                        nickname = contact.nickname,
                        passport = contact.passport,
                        personalMessage = contact.personalMessage ?: "",
                        status = contact.status.asStatus(),
                        null
                    )
                }
                model = model.copy(contacts = models)
                launch(Dispatchers.JavaFx) {
                    updateUI()
                }
            }
        }
        launch(Dispatchers.IO) {
            contactListRepository.profileUpdates().collect { profile ->
                profile?.let {
                    model = model.copy(
                        nickname = it.nickname,
                        passport = it.passport,
                        personalMessage = it.personalMessage ?: "",
                        status = it.status.asStatus()
                    )
                    launch(Dispatchers.JavaFx) {
                        updateUI()
                    }
                }
            }

        }
        launch(Dispatchers.IO) {
            contactListRepository.newMessages().collect { messages ->
                messages.forEach {
                    if (it.sender != model.passport) {
                        contactListRepository.markAsRead(it.id)
                        launch(Dispatchers.JavaFx) {
                            view.openConversation(it.sender)
                        }
                    }
                }
            }

        }
        launch(Dispatchers.IO) {
            contactListRepository.refreshContacts()
            profileRepository.changeStatus(Status.ONLINE)
            ConversationManager.start()
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
        launch(Dispatchers.IO) { profileRepository.changeStatus(status) }
        updateUI()
    }

    override fun onPersonalMessageChanged(text: String) {
        model = model.copy(personalMessage = text)
        launch(Dispatchers.IO) { profileRepository.updatePersonalMessage(text) }
    }

    override fun onCancelPersonalMessage() {
        view.setPersonalMessage(model.personalMessage)
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

    private fun String?.asStatus(): Status {
        return when (this) {
            "NLN" -> Status.ONLINE
            "BSY" -> Status.BUSY
            "IDL" -> Status.IDLE
            "BRB" -> Status.BE_RIGHT_BACK
            "AWY" -> Status.AWAY
            "PHN" -> Status.ON_THE_PHONE
            "LUN" -> Status.OUT_TO_LUNCH
            else -> Status.OFFLINE
        }
    }
}
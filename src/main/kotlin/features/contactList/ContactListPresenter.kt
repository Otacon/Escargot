package features.contactList

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import protocol.Status
import protocol.notification.ProfileData
import repositories.ContactListRepository
import repositories.ProfileRepository
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
        me = ContactModel.Contact(
            nickname = null,
            passport = "",
            personalMessage = "",
            status = Status.ONLINE,
            profilePicture = null
        ),
        filter = "",
        contacts = emptyList()
    )

    override fun start(passport: String) {
        model = model.copy(me = model.me.copy(passport = passport))
        launch(Dispatchers.IO) {
            contactListRepository.contactChanged.collect { profileData ->
                model = if (profileData.passport.equals(model.me.passport, true)) {
                    val self = updateContact(model.me, profileData)
                    model.copy(me = self)
                } else {
                    val updatedContacts = model.contacts.map {
                        if (it.passport.equals(profileData.passport, true)) {
                            updateContact(it, profileData)
                        } else {
                            it
                        }
                    }
                    model.copy(contacts = updatedContacts)
                }
                updateUI()
            }
        }
        launch(Dispatchers.IO) {
            val allContacts = contactListRepository.getContacts()

            val (me, others) = allContacts.partition { it.contactInfo.contactType.equals("Me", true) }

            me.firstOrNull()?.let {
                model = model.copy(me = model.me.copy(nickname = it.contactInfo.displayName))
            }
            val otherContacts = others.map {
                ContactModel.Contact(
                    nickname = it.contactInfo.displayName,
                    passport = it.contactInfo.passportName,
                    personalMessage = "",
                    status = Status.OFFLINE,
                    profilePicture = null
                )
            }
            model = model.copy(contacts = otherContacts)
            updateUI()
            profileRepository.changeStatus(model.me.status)
        }
    }

    private fun updateContact(contact: ContactModel.Contact, profileData: ProfileData): ContactModel.Contact {
        return contact.copy(
            nickname = profileData.nickname ?: contact.nickname,
            personalMessage = profileData.personalMessage ?: contact.personalMessage,
            status = profileData.status ?: contact.status
        )
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
        model = model.copy(me = model.me.copy(status = status))
        launch(Dispatchers.IO) { profileRepository.changeStatus(status) }
        updateUI()
    }

    override fun onPersonalMessageChanged(text: String) {
        model = model.copy(me = model.me.copy(personalMessage = text))
        launch(Dispatchers.IO) { profileRepository.changePersonalMessage(text) }
    }

    override fun onCancelPersonalMessage() {
        view.setPersonalMessage(model.me.personalMessage)
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setStatus(model.me.status)
        view.setProfilePicture(model.me.profilePicture)
        view.setNickname(model.me.nickname ?: model.me.passport)
        view.setPersonalMessage(model.me.personalMessage)

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
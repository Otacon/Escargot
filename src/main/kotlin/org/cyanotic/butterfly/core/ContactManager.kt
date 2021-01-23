package org.cyanotic.butterfly.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.cyanotic.butterfly.core.contact_list_fetcher.ContactListFetcher
import org.cyanotic.butterfly.database.ContactsTable
import org.cyanotic.butterfly.database.entities.ContactEntity
import org.cyanotic.butterfly.protocol.Status
import org.cyanotic.butterfly.protocol.asStatus
import org.cyanotic.butterfly.protocol.notification.ContactRequest
import org.cyanotic.butterfly.protocol.notification.ContactType
import org.cyanotic.butterfly.protocol.notification.ListType
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger("ContactManager")

class ContactManager(
    private val accountManager: AccountManager,
    private val localContacts: ContactsTable,
    private val notification: NotificationTransport,
    private val contactListFetcher: ContactListFetcher
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        listenForContactChanges()
    }

    private var contacts: List<Contact> = listOf()
    private val contactsUpdateChannel = BroadcastChannel<List<Contact>>(Channel.CONFLATED)

    suspend fun refreshContactList() {
        logger.info { "Refreshing contact list..." }
        val newContacts = contactListFetcher.getContacts(accountManager.mspAuth).mapNotNull {
            if (it.contactInfo.contactType.equals("me", true)) {
                null
            } else {
                Contact(
                    passport = it.contactInfo.passportName,
                    nickname = it.contactInfo.displayName,
                    personalMessage = "",
                    status = Status.OFFLINE
                )
            }
        }
        logger.debug { "Added/updated contacts: $newContacts" }
        contacts = newContacts
        contactsUpdateChannel.offer(contacts)
    }

    suspend fun getContacts(): List<Contact> {
        return contacts
    }

    suspend fun addContact(passport: String) {
        logger.info { "Adding new contact: $passport" }
        notification.sendAdl(passport, ListType.AddList, ContactType.Passport)
        val currentAccount = accountManager.account
        val mspAuth = accountManager.mspAuth
        val ownContact = localContacts.getByPassport(currentAccount)
        val nickname = ownContact!!.nickname ?: currentAccount
        contactListFetcher.addContact(currentAccount, mspAuth, nickname)
    }

    fun otherContactsUpdates(): Flow<List<Contact>> = this.contactsUpdateChannel.asFlow()

    fun contactRequestReceived(): Flow<ContactRequest> {
        return notification.contactRequests().mapNotNull {
            val contact = localContacts.getByPassport(it.passport)
            if (contact == null) {
                it
            } else {
                null
            }
        }
    }

    private fun listenForContactChanges() {
        launch {
            notification.contactChanged()
                .onEach { logger.debug { "Contact Changed: $it" } }
                .filter { !it.passport.equals(accountManager.account, true) }
                .onEach { update ->
                    contacts = contacts.map { contact ->
                        if (update.passport.equals(contact.passport, true)) {
                            val newContact = Contact(
                                passport = update.passport,
                                nickname = update.nickname ?: contact.nickname,
                                personalMessage = update.personalMessage ?: contact.personalMessage,
                                status = update.status?.asStatus() ?: contact.status
                            )
                            newContact
                        } else {
                            contact
                        }
                    }
                }
                .onEach { contactsUpdateChannel.offer(contacts) }
                .collect()
        }
    }

    suspend fun getContact(passport: String): ContactEntity? {
        return localContacts.getByPassport(passport)
    }
}

data class Contact(
    val passport: String,
    val nickname: String,
    val personalMessage: String,
    val status: Status
)
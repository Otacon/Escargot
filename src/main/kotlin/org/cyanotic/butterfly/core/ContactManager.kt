package org.cyanotic.butterfly.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.cyanotic.butterfly.core.contactListFetcher.ContactListFetcher
import org.cyanotic.butterfly.core.utils.httpClient
import org.cyanotic.butterfly.database.ContactsTable
import org.cyanotic.butterfly.database.entities.Contact
import org.cyanotic.butterfly.protocol.notification.ContactType
import org.cyanotic.butterfly.protocol.notification.ListType
import org.cyanotic.butterfly.protocol.notification.NotificationTransportManager
import kotlin.coroutines.CoroutineContext

object ContactManager : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val localContacts = ContactsTable()
    private val contactListFetcher = ContactListFetcher(httpClient)
    private val accountManager = AccountManager
    private val notificationTransportManager = NotificationTransportManager

    init {
        listenForNotificationContactChanges()
    }

    suspend fun refreshContactList() {
        val currentAccount = accountManager.getCurrentAccount()
        val newContacts = contactListFetcher.getContacts(currentAccount.mspauth!!).map {
            Contact(
                passport = it.contactInfo.passportName,
                account = currentAccount.passport,
                nickname = it.contactInfo.displayName,
                personalMessage = null,
                status = null
            )
        }
        localContacts.update(currentAccount.passport, newContacts)
    }

    suspend fun addContact(passport: String) {
        notificationTransportManager.transport.sendAdl(passport, ListType.AddList, ContactType.Passport)
        val currentAccount = accountManager.getCurrentAccount()
        val ownContact = localContacts.getByPassport(currentAccount.passport, currentAccount.passport)
        val nickname = ownContact.nickname ?: currentAccount.passport
        contactListFetcher.addContact(passport, currentAccount.mspauth!!, nickname)
    }

    suspend fun ownContactUpdates(): Flow<Contact> {
        return localContacts.ownContactUpdates(accountManager.getCurrentAccount().passport)
    }

    suspend fun otherContactsUpdates(): Flow<List<Contact>> {
        return localContacts.otherContactsUpdates(accountManager.getCurrentAccount().passport)
    }

    private fun listenForNotificationContactChanges() {
        launch {
            notificationTransportManager.transport.contactChanged().collect { profileData ->
                val account = accountManager.getCurrentAccount().passport
                val updatedContact = Contact(
                    passport = profileData.passport,
                    account = account,
                    nickname = profileData.nickname,
                    personalMessage = profileData.personalMessage,
                    status = profileData.status
                )
                localContacts.update(account, listOf(updatedContact))
            }
        }
    }
}
package org.cyanotic.butterfly.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.cyanotic.butterfly.core.contact_list_fetcher.ContactListFetcher
import org.cyanotic.butterfly.core.utils.httpClient
import org.cyanotic.butterfly.database.ContactsTable
import org.cyanotic.butterfly.database.entities.Contact
import org.cyanotic.butterfly.protocol.notification.ContactRequest
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

    suspend fun setAllContactsOffline(){
        val accountPassport = accountManager.getCurrentAccount().passport
        localContacts.setAllOffline(accountPassport)
    }

    suspend fun refreshContactList() {
        val currentAccount = accountManager.getCurrentAccount()
        val accountPassport = currentAccount.passport
        val newContacts = contactListFetcher.getContacts(currentAccount.mspauth!!).map {
            Contact(
                passport = it.contactInfo.passportName,
                account = accountPassport,
                nickname = it.contactInfo.displayName,
                personalMessage = null,
                status = null
            )
        }
        val removedPassports = localContacts.getAll(accountPassport).mapNotNull { localContact ->
            val existingLocalContact = newContacts.firstOrNull { it.passport == localContact.passport }
            if (existingLocalContact == null) {
                localContact.passport
            } else {
                null
            }
        }
        localContacts.removeAll(accountPassport, removedPassports)
        localContacts.update(accountPassport, newContacts)
    }

    suspend fun addContact(passport: String) {
        notificationTransportManager.transport.sendAdl(passport, ListType.AddList, ContactType.Passport)
        val currentAccount = accountManager.getCurrentAccount()
        val ownContact = localContacts.getByPassport(currentAccount.passport, currentAccount.passport)
        val nickname = ownContact!!.nickname ?: currentAccount.passport
        contactListFetcher.addContact(passport, currentAccount.mspauth!!, nickname)
    }

    suspend fun ownContactUpdates(): Flow<Contact> {
        return localContacts.ownContactUpdates(accountManager.getCurrentAccount().passport)
    }

    suspend fun otherContactsUpdates(): Flow<List<Contact>> {
        return localContacts.otherContactsUpdates(accountManager.getCurrentAccount().passport)
    }

    suspend fun contactRequestReceived(): Flow<ContactRequest> {
        return notificationTransportManager.transport.contactRequests().mapNotNull {
            val currentAccount = accountManager.getCurrentAccount()
            val contact = localContacts.getByPassport(currentAccount.passport, it.passport)
            if(contact == null){
                it
            } else {
                null
            }
        }
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

    suspend fun getContact(passport: String): Contact? {
        val account = accountManager.getCurrentAccount().passport
        return localContacts.getByPassport(account, passport)
    }
}
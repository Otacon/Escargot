package org.cyanotic.butterfly.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.cyanotic.butterfly.core.contact_list_fetcher.ContactListFetcher
import org.cyanotic.butterfly.database.ContactsTable
import org.cyanotic.butterfly.features.Contact
import org.cyanotic.butterfly.protocol.asStatus
import org.cyanotic.butterfly.database.entities.Contact as ContactEntity
import org.cyanotic.butterfly.protocol.notification.ContactRequest
import org.cyanotic.butterfly.protocol.notification.ContactType
import org.cyanotic.butterfly.protocol.notification.ListType
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import kotlin.coroutines.CoroutineContext

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
        listenForNotificationContactChanges()
    }

    private val contacts: List<Contact> = mutableListOf()

    suspend fun setAllContactsOffline() {
        localContacts.setAllOffline(accountManager.account)
    }

    suspend fun refreshContactList() {
        val accountPassport = accountManager.account
        val newContacts = contactListFetcher.getContacts(accountManager.mspAuth).map {
            if(it.contactInfo.contactType.equals("me",true)){
                accountManager.accountUpdated(null,it.contactInfo.displayName,null)
            }
            ContactEntity(
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
        notification.sendAdl(passport, ListType.AddList, ContactType.Passport)
        val currentAccount = accountManager.account
        val mspAuth = accountManager.mspAuth
        val ownContact = localContacts.getByPassport(currentAccount, currentAccount)
        val nickname = ownContact!!.nickname ?: currentAccount
        contactListFetcher.addContact(currentAccount, mspAuth, nickname)
    }

    suspend fun otherContactsUpdates(): Flow<List<ContactEntity>> {
        return localContacts.otherContactsUpdates(accountManager.account)
    }

    suspend fun contactRequestReceived(): Flow<ContactRequest> {
        return notification.contactRequests().mapNotNull {
            val contact = localContacts.getByPassport(accountManager.account, it.passport)
            if (contact == null) {
                it
            } else {
                null
            }
        }
    }

    private fun listenForNotificationContactChanges() {
        launch {
            notification.contactChanged().collect { profileData ->
                if(profileData.passport.equals(accountManager.account,true)){
                    accountManager.accountUpdated(
                        profileData.status?.asStatus(),
                        profileData.nickname,
                        profileData.personalMessage
                    )
                }
                val account = accountManager.account
                val updatedContact = ContactEntity(
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

    suspend fun getContact(passport: String): ContactEntity? {
        val account = accountManager.account
        return localContacts.getByPassport(account, passport)
    }
}
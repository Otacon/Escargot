package org.cyanotic.butterfly.features.contact_list

import org.cyanotic.butterfly.core.ButterflyClient
import org.cyanotic.butterfly.protocol.Status

class ContactListInteractor(
    private val client: ButterflyClient
) {

    suspend fun getAllContacts() = client.getContactManager().getContacts()

    suspend fun otherContactsUpdates() = client.getContactManager().otherContactsUpdates()

    suspend fun ownContactUpdates() = client.getAccountManager().accountUpdates()

    suspend fun changeStatus(status: Status) {
        client.getAccountManager().setStatus(status)
    }

    suspend fun updatePersonalMessage(text: String) {
        client.getAccountManager().setPersonalMessage(text)
    }

    suspend fun refreshContactList() {
        client.getContactManager().refreshContactList()
    }

    suspend fun newContactRequests() = client.getContactManager().contactRequestReceived()

    suspend fun disconnect() {
        client.getAccountManager().setStatus(Status.OFFLINE)
        client.disconnect()
    }

    suspend fun allIncomingMessages() = client.getConversationManager().allIncomingMessages()
}
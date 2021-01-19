package org.cyanotic.butterfly.features.contact_list

import kotlinx.coroutines.flow.Flow
import org.cyanotic.butterfly.core.AccountManager
import org.cyanotic.butterfly.core.ContactManager
import org.cyanotic.butterfly.core.ConversationManager
import org.cyanotic.butterfly.database.entities.Conversation
import org.cyanotic.butterfly.protocol.Status
import org.cyanotic.butterfly.protocol.notification.ContactRequest

class ContactListInteractor(
    private val contactManager: ContactManager,
    private val accountManager: AccountManager,
    private val conversationManager: ConversationManager
) {

    suspend fun otherContactsUpdates() = contactManager.otherContactsUpdates()

    suspend fun ownContactUpdates() = contactManager.ownContactUpdates()

    suspend fun changeStatus(status: Status) {
        accountManager.setStatus(status)
    }

    suspend fun updatePersonalMessage(text: String) {
        accountManager.setPersonalMessage(text)
    }

    suspend fun newMessagesForConversation(): Flow<Conversation> {
        return conversationManager.newMessage()
    }

    suspend fun refreshContactList() {
        contactManager.refreshContactList()
    }

    suspend fun newContactRequests() = contactManager.contactRequestReceived()
}
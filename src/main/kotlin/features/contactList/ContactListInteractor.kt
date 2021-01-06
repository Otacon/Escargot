package features.contactList

import core.AccountManager
import core.ContactManager
import core.ConversationManager
import kotlinx.coroutines.flow.Flow
import me.orfeo.Conversation
import protocol.Status

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
}
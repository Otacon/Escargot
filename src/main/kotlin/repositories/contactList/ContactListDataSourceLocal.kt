package repositories.contactList

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.orfeo.Contact
import me.orfeo.Database
import me.orfeo.Message

class ContactListDataSourceLocal(
    private val database: Database
) {

    suspend fun addContacts(contacts: List<Contact>) {
        database.contactsQueries.transaction {
            contacts.forEach {
                database.contactsQueries.add(it)
            }
        }
    }

    suspend fun contactsUpdates(passport: String) = database.contactsQueries.getAll(passport).asFlow().mapToList()

    suspend fun profileUpdates(passport: String) =
        database.contactsQueries.getByPassport(passport, passport).asFlow().map { it.executeAsOneOrNull() }

    suspend fun getAccountByPassport(account: String, passport: String) =
        database.contactsQueries.getByPassport(account, passport).executeAsOneOrNull()

    suspend fun newMessages(): Flow<List<Message>> = database.messagesQueries.getNewMessages().asFlow().mapToList()

    suspend fun markMessageAsRead(id: Long) = database.messagesQueries.markAsSynced(id)

}
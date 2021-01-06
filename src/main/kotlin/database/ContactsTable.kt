package database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import me.orfeo.Contact

class ContactsTable {

    private val queries = MSNDB.db.contactsQueries

    suspend fun update(account: String, newContacts: List<Contact>) {
        queries.transaction {
            newContacts.map { updatedContact ->
                val existingContact = queries.getByPassport(account, updatedContact.passport).executeAsOneOrNull()
                existingContact?.copy(
                    nickname = updatedContact.nickname ?: existingContact.nickname,
                    personalMessage = updatedContact.personalMessage ?: existingContact.personalMessage,
                    status = updatedContact.status ?: existingContact.status
                ) ?: Contact(
                    passport = updatedContact.passport,
                    account = updatedContact.account,
                    nickname = updatedContact.nickname,
                    personalMessage = updatedContact.personalMessage,
                    status = updatedContact.status
                )
            }.forEach {
                queries.insertOrUpdate(it)
            }

        }
    }

    fun ownContactUpdates(passport: String): Flow<Contact> {
        return queries.getByPassport(passport, passport).asFlow().mapToOneNotNull()
    }

    fun otherContactsUpdates(passport: String): Flow<List<Contact>> {
        return queries.getAll(passport).asFlow().mapToList()
    }
}
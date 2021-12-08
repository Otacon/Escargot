package org.cyanotic.butterfly.database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import org.cyanotic.butterfly.database.entities.ContactEntity
import org.cyanotic.butterfly.database.entities.ContactsQueries

class ContactsTable(
    private val queries : ContactsQueries
) {

    fun update(account: String, newContacts: List<ContactEntity>) {
        newContacts.map { updatedContact ->
            val existingContact = queries.getByPassport(updatedContact.passport).executeAsOneOrNull()
            existingContact?.copy(
                nickname = updatedContact.nickname ?: existingContact.nickname,
                personalMessage = updatedContact.personalMessage ?: existingContact.personalMessage
            ) ?: ContactEntity(
                passport = updatedContact.passport,
                nickname = updatedContact.nickname,
                personalMessage = updatedContact.personalMessage,
            )
        }.forEach {
            queries.insertOrUpdate(it)
        }
    }

    fun ownContactUpdates(passport: String): Flow<ContactEntity> {
        return queries.getByPassport(passport).asFlow().mapToOneNotNull()
    }

    fun otherContactsUpdates(): Flow<List<ContactEntity>> {
        return queries.getAll().asFlow().mapToList()
    }

    fun getByPassport(passport: String): ContactEntity? {
        return queries.getByPassport(passport).executeAsOneOrNull()
    }

    fun getAll(): List<ContactEntity> {
        return queries.getAll().executeAsList()
    }

    fun removeAll(removedPassports: List<String>) {
        removedPassports.forEach {
            queries.remove(it)
        }
    }
}
package repositories.contactList

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import me.orfeo.Contact
import repositories.profile.ProfileDataSourceLocal

class ContactListRepository(
    private val remote: ContactListDataSourceRemote,
    private val local: ContactListDataSourceLocal,
    private val profileLocal: ProfileDataSourceLocal
) {

    suspend fun startListeningForAccountChanges() {
        remote.contactChanged().collect { changes ->
            val passport = changes.passport
            val me = profileLocal.getCurrentPassport()
            val contact = local.getAccountByPassport(me, passport)
            contact?.let {
                val updatedContact = contact.copy(
                    nickname = changes.nickname ?: contact.nickname,
                    personalMessage = changes.personalMessage ?: contact.personalMessage,
                    status = changes.status ?: contact.status
                )

                local.addContacts(listOf(updatedContact))
            }
        }
    }

    suspend fun refreshContacts() {
        val passport = profileLocal.getCurrentPassport()
        val msnpAuth = profileLocal.getMsnpAuth()
        val contacts = remote.getContacts(msnpAuth).map {
            Contact(
                it.contactInfo.passportName,
                passport,
                it.contactInfo.displayName,
                null,
                null,
                null
            )
        }
        local.addContacts(contacts)
    }

    suspend fun contactUpdates(): Flow<List<Contact>> {
        val passport = profileLocal.getCurrentPassport()
        return local.contactsUpdates(passport)
    }

    suspend fun profileUpdates(): Flow<Contact?> {
        val passport = profileLocal.getCurrentPassport()
        return local.profileUpdates(passport)
    }

}
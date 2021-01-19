package org.cyanotic.butterfly.features.add_contact

import org.cyanotic.butterfly.core.ContactManager

class AddContactInteractor(
    private val contactManager: ContactManager
) {

    suspend fun addContact(passport: String){
        contactManager.addContact(passport)
    }

}
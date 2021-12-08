package org.cyanotic.butterfly.features.add_contact

import org.cyanotic.butterfly.core.ButterflyClient

class AddContactInteractor(
    private val msnClient: ButterflyClient
) {

    suspend fun addContact(passport: String){
        msnClient.getContactManager().addContact(passport)
    }

}
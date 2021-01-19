package org.cyanotic.butterfly.features.friend_request

import org.cyanotic.butterfly.core.ContactManager

class FriendRequestInteractor(
    private val contactManager: ContactManager
) {
    suspend fun addContact(passport: String) {
        contactManager.addContact(passport)
    }
}
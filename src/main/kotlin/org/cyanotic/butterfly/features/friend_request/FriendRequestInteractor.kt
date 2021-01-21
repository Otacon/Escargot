package org.cyanotic.butterfly.features.friend_request

import org.cyanotic.butterfly.core.ButterflyClient

class FriendRequestInteractor(
    private val client: ButterflyClient
) {
    suspend fun addContact(passport: String) {
        client.getContactManager().addContact(passport)
    }
}
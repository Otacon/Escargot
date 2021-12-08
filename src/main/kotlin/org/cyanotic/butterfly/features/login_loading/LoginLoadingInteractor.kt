package org.cyanotic.butterfly.features.login_loading

import org.cyanotic.butterfly.core.ButterflyClient
import org.cyanotic.butterfly.core.auth.AuthenticationResult
import org.cyanotic.butterfly.protocol.Status

class LoginLoadingInteractor(
    private val client: ButterflyClient
) {

    suspend fun login(username: String, password: String): AuthenticationResult {
        client.connect()
        return client.authenticate(username, password)
    }

    suspend fun updateStatus(online: Status) {
        client.getAccountManager().setStatus(online)
    }

    suspend fun refreshContactList() {
        client.getContactManager().refreshContactList()
    }

}
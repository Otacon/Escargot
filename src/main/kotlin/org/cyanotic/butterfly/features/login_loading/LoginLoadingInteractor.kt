package org.cyanotic.butterfly.features.login_loading

import org.cyanotic.butterfly.core.AccountManager
import org.cyanotic.butterfly.core.ContactManager
import org.cyanotic.butterfly.core.auth.AuthenticationResult
import org.cyanotic.butterfly.protocol.Status

class LoginLoadingInteractor(
    private val accountManager: AccountManager,
    private val contactManager: ContactManager
) {

    suspend fun login(username: String, password: String): AuthenticationResult {
        return accountManager.authenticate(username, password)
    }

    suspend fun updateStatus(online: Status) {
        accountManager.setStatus(online)
    }

    suspend fun refreshContactList() {
        contactManager.setAllContactsOffline()
        contactManager.refreshContactList()
    }

}
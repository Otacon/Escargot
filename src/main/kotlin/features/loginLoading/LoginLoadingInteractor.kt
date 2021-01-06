package features.loginLoading

import core.AccountManager
import core.AuthenticationResult
import core.ContactManager
import protocol.Status

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
        contactManager.refreshContactList()
    }

}
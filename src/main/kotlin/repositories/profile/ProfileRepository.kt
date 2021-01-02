package repositories.profile

import me.orfeo.Account
import protocol.Status

class ProfileRepository(
    private val remote: ProfileDataSourceRemote,
    private val local: ProfileDataSourceLocal
) {

    suspend fun authenticate(username: String, password: String): AuthenticationResult {
        return remote.authenticate(username, password)
    }

    suspend fun saveAccount(
        username: String,
        password: String,
        mspAuth: String,
        rememberUser: Boolean,
        rememberPassword: Boolean,
        autoSignin: Boolean
    ) {
        local.saveUser(username, password, mspAuth, rememberUser, rememberPassword, autoSignin)
    }

    suspend fun getLatestUsedAccounts(): List<Account> {
        return local.getLastUsed()
    }

    suspend fun changeStatus(status: Status) {
        try {
            remote.changeStatus(status)
            local.changeStatus(status)
        } catch (e: Exception) {

        }
    }

    suspend fun updatePersonalMessage(text: String) {
        remote.updatePersonalMessage(text)
        local.updatePersonalMessage(text)
    }

    suspend fun getAccountByPassport(passport: String) : Account? {
        return local.getAccountByPassport(passport)
    }

}


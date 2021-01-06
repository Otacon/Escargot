package features.login

import core.AccountManager
import me.orfeo.Account

class LoginInteractor(
    private val accountManager: AccountManager
) {

    suspend fun getSavedUsernames(): List<String> {
        return accountManager.getAccounts().map { it.passport }
    }

    suspend fun getLastUsedAccount(): Account? {
        return accountManager.getAccounts().firstOrNull()
    }

    suspend fun getAccountByPassport(passport: String?): Account? {
        return accountManager.getAccounts().firstOrNull { it.passport.equals(passport, true) }
    }

    suspend fun updateLoginPreferences(
        savePassword: Boolean,
        password: String,
        rememberUser: Boolean,
        autoSignin: Boolean
    ) {
        accountManager.saveLoginPreferences(
            password = if (savePassword) password else null,
            temporary = rememberUser.not(),
            autoSignIn = autoSignin
        )
    }

}
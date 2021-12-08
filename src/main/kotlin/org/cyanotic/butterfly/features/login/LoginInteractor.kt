package org.cyanotic.butterfly.features.login

import org.cyanotic.butterfly.core.global_settings.GlobalSettingsManager
import org.cyanotic.butterfly.core.global_settings.StoredAccount
import org.cyanotic.butterfly.features.Account

class LoginInteractor(
    private val globalSettingsManager: GlobalSettingsManager
) {

    suspend fun getSavedUsernames(): List<String> {
        return globalSettingsManager.accounts
            .map { it.passport }
    }

    suspend fun getLastUsedAccount(): Account? {
        return globalSettingsManager.accounts
            .map { it.toAccountModel() }
            .maxBy { it.lastLogin }
    }

    suspend fun getAccountByPassport(passport: String?): Account? {
        return globalSettingsManager.accounts
            .map { it.toAccountModel() }
            .firstOrNull { it.passport.equals(passport, true) }
    }

    suspend fun updateLoginPreferences(
        passport: String,
        savePassword: Boolean,
        password: String,
        rememberUser: Boolean,
        autoSignin: Boolean
    ) {
        GlobalSettingsManager.addOrUpdateAccount(StoredAccount(
            passport = passport,
            password = password,
            savePassword = savePassword,
            saveUsername = rememberUser,
            loginAutomatically = autoSignin,
            lastLogin = System.currentTimeMillis()
        ))
    }

    private fun StoredAccount.toAccountModel() : Account{
        return Account(
            passport = passport,
            password = password,
            lastLogin = lastLogin,
            rememberPassport = saveUsername,
            rememberPassword = savePassword,
            loginAutomatically = loginAutomatically,
        )
    }
}
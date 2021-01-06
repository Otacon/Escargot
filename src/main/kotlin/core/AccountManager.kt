package core

import core.auth.MSNPAuthenticatorFactory
import database.AccountsTable
import database.ContactsTable
import database.StatusEntity
import me.orfeo.Account
import me.orfeo.Contact
import protocol.Status
import protocol.asString
import protocol.notification.NotificationSendCommand
import protocol.notification.NotificationTransportManager
import protocol.notification.TransportException

object AccountManager {

    private val localAccounts = AccountsTable()
    private val localContacts = ContactsTable()
    private val authenticator = MSNPAuthenticatorFactory().createAuthenticator()
    private val notificationService = NotificationTransportManager.transport

    private var currentAccount: String? = null

    suspend fun authenticate(username: String, password: String): AuthenticationResult {
        return when (val result = authenticator.authenticate(username, password)) {
            AuthenticationResult.UnsupportedProtocol,
            AuthenticationResult.InvalidPassword,
            AuthenticationResult.InvalidUser,
            AuthenticationResult.ServerError -> result
            is AuthenticationResult.Success -> {
                currentAccount = username.toLowerCase()
                localAccounts.add(
                    passport = username,
                    mspAuth = result.token
                )
                result
            }
        }
    }

    suspend fun saveLoginPreferences(password: String?, temporary: Boolean, autoSignIn: Boolean) {
        localAccounts.updateLoginPreferences(
            passport = currentAccount!!,
            password = password,
            temporary = temporary,
            autoSignIn = autoSignIn
        )
    }

    suspend fun setStatus(status: Status) {
        val passport = currentAccount!!
        val entity = when (status) {
            Status.ONLINE -> StatusEntity.ONLINE
            Status.AWAY -> StatusEntity.AWAY
            Status.BE_RIGHT_BACK -> StatusEntity.BE_RIGHT_BACK
            Status.IDLE -> StatusEntity.IDLE
            Status.OUT_TO_LUNCH -> StatusEntity.OUT_TO_LUNCH
            Status.ON_THE_PHONE -> StatusEntity.ON_THE_PHONE
            Status.BUSY -> StatusEntity.BUSY
            Status.OFFLINE -> StatusEntity.OFFLINE
            Status.HIDDEN -> StatusEntity.HIDDEN
        }
        val originalStatus = localAccounts.getByPassport(passport).status
        localAccounts.updateStatus(passport, entity)
        try {
            val command = NotificationSendCommand.CHG(status.asString())
            notificationService.sendChg(command)
        } catch (e: TransportException) {
            localAccounts.updateStatus(passport, originalStatus)
            e.printStackTrace()
        }
    }

    suspend fun setNickname(nickname: String) {

    }

    suspend fun setPersonalMessage(personalMessage: String) {
        val account = currentAccount!!
        val update = Contact(
            passport = account,
            account = account,
            nickname = null,
            personalMessage = personalMessage,
            status = null
        )
        notificationService.sendUux(personalMessage)
        localContacts.update(account, listOf(update))
    }

    suspend fun getCurrentAccount(): Account {
        return localAccounts.getByPassport(currentAccount!!)
    }

    suspend fun getAccounts(): List<Account> {
        return localAccounts.getAllOrderedByLastLogin()
    }

    suspend fun logout() {
        currentAccount = null
    }

    suspend fun updates() = localAccounts.updates(currentAccount!!)

}
package repositories.profile

import me.orfeo.Account
import me.orfeo.Database
import protocol.Status

class ProfileDataSourceLocal(
    private val database: Database
) {

    suspend fun saveUser(
        username: String,
        password: String,
        mspauth: String,
        rememberUser: Boolean,
        rememberPassword: Boolean,
        autoSignin: Boolean
    ) {
        val finalPassword = if (rememberPassword) password else null
        database.accountsQueries.addAccount(
            passport = username,
            password = finalPassword,
            mspauth = mspauth,
            temporary = rememberUser.not(),
            last_login = System.currentTimeMillis(),
            auto_sigin = autoSignin
        )
    }

    suspend fun getLastUsed(): List<Account> {
        return database.accountsQueries.getLastUsed().executeAsList()
    }

    suspend fun changeStatus(status: Status) {
        val passport = database.accountsQueries.getLastUsed().executeAsOne().passport
        val literalStatus = when (status) {
            Status.ONLINE -> "NLN"
            Status.AWAY -> "AWY"
            Status.BE_RIGHT_BACK -> "BRB"
            Status.IDLE -> "IDL"
            Status.OUT_TO_LUNCH -> "LUN"
            Status.ON_THE_PHONE -> "PHN"
            Status.BUSY -> "BSY"
            Status.OFFLINE -> "FLN"
            Status.HIDDEN -> "HDN"
        }
        database.accountsQueries.updateStatus(literalStatus, passport)
    }

    suspend fun getMsnpAuth(): String {
        return database.accountsQueries.getCurrent().executeAsList().first().mspauth!!
    }

    suspend fun getCurrentPassport(): String {
        return database.accountsQueries.getCurrent().executeAsList().first().passport
    }

    suspend fun updatePersonalMessage(text: String) {
        //TODO
    }

    suspend fun getAccountByPassport(passport: String): Account? {
        return database.accountsQueries.getByPassport(passport).executeAsOneOrNull()
    }
}
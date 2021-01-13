package org.cyanotic.butterfly.database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import org.cyanotic.butterfly.database.entities.Account

class AccountsTable {

    private val queries = MSNDB.db.accountsQueries

    fun getAllOrderedByLastLogin(): List<Account> {
        return queries.selectAllOrderedByLastLogin().executeAsList()
    }

    fun add(passport: String, mspAuth: String) {
        queries.insert(
            passport = passport.toLowerCase(),
            mspauth = mspAuth,
            last_login = System.currentTimeMillis()
        )
    }

    fun updateLoginPreferences(passport: String, password: String?, temporary: Boolean, autoSignIn: Boolean) {

        queries.updateLoginPreferences(
            passport = passport,
            password = password,
            temporary = temporary,
            auto_sigin = autoSignIn
        )
    }

    fun updateStatus(passport: String, status: StatusEntity) {
        queries.updateStatus(status, passport)
    }

    fun clearTemporaryAccounts() {

        queries.clearTemporaryAccounts()
    }

    fun getByPassport(passport: String): Account {
        return queries.selectByPassport(passport.toLowerCase()).executeAsOne()
    }

    fun updates(passport: String) = queries.selectByPassport(passport).asFlow().mapToOne()

}
package database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import me.orfeo.Account

class AccountsTable {

    private val queries = MSNDB.db.accountsQueries

    fun getAllOrderedByLastLogin(): List<Account> {
        return queries.transactionWithResult {
            queries.selectAllOrderedByLastLogin().executeAsList()
        }
    }

    fun add(passport: String, mspAuth: String) {
        queries.transaction {
            queries.insert(
                passport = passport.toLowerCase(),
                mspauth = mspAuth,
                last_login = System.currentTimeMillis()
            )
        }
    }

    fun updateLoginPreferences(passport: String, password: String?, temporary: Boolean, autoSignIn: Boolean) {
        queries.transaction {
            queries.updateLoginPreferences(
                passport = passport,
                password = password,
                temporary = temporary,
                auto_sigin = autoSignIn
            )
        }
    }

    fun updateStatus(passport: String, status: StatusEntity) {
        queries.transaction {
            queries.updateStatus(status, passport)
        }
    }

    fun clearTemporaryAccounts() {
        queries.transaction {
            queries.clearTemporaryAccounts()
        }
    }

    fun getByPassport(passport: String): Account {
        return queries.transactionWithResult {
            queries.selectByPassport(passport.toLowerCase()).executeAsOne()
        }
    }

    fun updates(passport: String) = queries.selectByPassport(passport).asFlow().mapToOne()

}
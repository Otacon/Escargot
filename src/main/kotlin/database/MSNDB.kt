package database

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import me.orfeo.Database
import me.orfeo.Database.Companion.Schema
import me.orfeo.Database.Companion.invoke


object MSNDB {

    val db by lazy {
        init()
    }

    private fun init(): Database {
        val driver = JdbcSqliteDriver("jdbc:sqlite:escargot.db")
        val currentVer = getVersion(driver)
        if (currentVer == 0) {
            Schema.create(driver)
            setVersion(driver, 1)
        } else {
            val schemaVer = Schema.version
            if (schemaVer > currentVer) {
                Schema.migrate(driver, currentVer, schemaVer)
                setVersion(driver, schemaVer)
            }
        }
        return invoke(driver)
    }

    private fun getVersion(driver: SqlDriver): Int {
        val sqlCursor: SqlCursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
        return sqlCursor.getLong(0)!!.toInt()
    }

    private fun setVersion(driver: SqlDriver, version: Int) {
        driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
    }
}



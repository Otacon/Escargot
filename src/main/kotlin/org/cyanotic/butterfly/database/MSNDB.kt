package org.cyanotic.butterfly.database

import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.cyanotic.butterfly.core.fileManager.fileManager
import org.cyanotic.butterfly.database.Database.Companion.Schema
import org.cyanotic.butterfly.database.Database.Companion.invoke
import org.cyanotic.butterfly.database.entities.Account
import java.io.File


object MSNDB {

    private const val DATABASE_FILE = "escargot.sqlite3"
    var path = fileManager.appHomePath
    val db by lazy {
        init()
    }

    private fun init(): Database {
        val databasePath = File(path, DATABASE_FILE).absolutePath
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")
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
        return invoke(
            driver,
            Account.Adapter(
                StatusAdapter()
            )
        )
    }

    private fun getVersion(driver: SqlDriver): Int {
        val sqlCursor: SqlCursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
        return sqlCursor.getLong(0)!!.toInt()
    }

    private fun setVersion(driver: SqlDriver, version: Int) {
        driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
    }
}



package org.cyanotic.butterfly

import javafx.application.Application.launch
import org.cyanotic.butterfly.core.fileManager.fileManager
import org.cyanotic.butterfly.database.AccountsTable
import org.cyanotic.butterfly.features.EscargotApplication
import java.io.File


fun main() {
    MessengerLauncher().start()
}

class MessengerLauncher {

    fun start() {
        val logFolder = File(fileManager.appHomePath, "logs").apply {
            if (!exists()) {
                mkdir()
            }
        }
        val dateTime = System.currentTimeMillis()
        val logFile = File(logFolder, "log_$dateTime.log")
        System.setProperty("log.path", logFile.absolutePath)
        AccountsTable().apply { clearTemporaryAccounts() }
        try {
            launch(EscargotApplication::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
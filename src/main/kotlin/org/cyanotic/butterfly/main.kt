package org.cyanotic.butterfly

import javafx.application.Application.launch
import org.cyanotic.butterfly.core.file_manager.fileManager
import org.cyanotic.butterfly.features.EscargotApplication
import java.io.File
import java.net.InetAddress


fun main() {
    val computerName = try {
        InetAddress.getLocalHost().hostName
    } catch(e: Exception) {
        println(e)
        null
    }
    println("Computer name: $computerName")
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
        //AccountsTable().apply { clearTemporaryAccounts() }
        try {
            launch(EscargotApplication::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
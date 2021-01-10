import core.fileManager.fileManager
import database.AccountsTable
import features.EscargotApplication
import javafx.application.Application.launch
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
        val dateTime = System.currentTimeMillis().toString()
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
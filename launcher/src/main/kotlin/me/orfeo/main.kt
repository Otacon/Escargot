package me.orfeo

import me.orfeo.mainWindow.MainWindowView
import me.orfeo.utils.fileManager
import java.io.File
import javax.swing.UIManager


fun main() {
    val logFolder = File(fileManager.appHomePath, "logs").apply {
        if (!exists()) {
            mkdir()
        }
    }
    val dateTime = System.currentTimeMillis().toString()
    val logFile = File(logFolder, "log_launcher_$dateTime.log")
    System.setProperty("log.path", logFile.absolutePath)

    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception){

    }
    MainWindowView().show()
}


package me.orfeo.mainWindow

import org.update4j.Archive
import org.update4j.Configuration
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import org.update4j.UpdateOptions
import org.update4j.service.UpdateHandler
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

import java.nio.file.Paths


class MainWindowInteractor {

    fun getConfiguration(): Configuration? {
        val configUrl = URL("https://srv-store3.gofile.io/download/SxUqZi/update4jconfig.xml")
        return try {
            InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8).use { Configuration.read(it) }
        } catch (e: IOException) {
            null
        }
    }

    fun performUpdate(config: Configuration, appHome: String, progress: (Float) -> Unit): Boolean {
        val zip = Paths.get(appHome,"escargot-update.zip")
        val updateHandler = object : UpdateHandler {
            override fun updateDownloadProgress(frac: Float) {
                progress(frac)
            }
        }
        val exception = config.update(UpdateOptions.archive(zip).updateHandler(updateHandler)).exception
        return if (exception == null) {
            Archive.read(zip).install()
            true
        } else {
            exception.printStackTrace()
            false
        }
    }

    fun clearData(appHome: String) {
        val appHomeDirectory = Paths.get(appHome).toFile()
        appHomeDirectory.deleteRecursively()
        appHomeDirectory.mkdirs()
    }
}
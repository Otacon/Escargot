package me.orfeo.mainWindow

import mu.KotlinLogging
import org.update4j.Archive
import org.update4j.Configuration
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import org.update4j.UpdateOptions
import org.update4j.service.UpdateHandler
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

class MainWindowInteractor {

    fun getConfiguration(): Configuration? {
        val configUrl = URL("https://srv-store3.gofile.io/download/SxUqZi/update4jconfig.xml")
        return try {
            InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8).use { Configuration.read(it) }
        } catch (e: IOException) {
            logger.error("Unable to download update configuration.", e)
            e.printStackTrace()
            null
        }
    }

    fun performUpdate(config: Configuration, appHome: String, progress: (Float) -> Unit): Boolean {
        val zip = Paths.get(appHome, "escargot-update.zip")
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
            logger.error("Unable to perform update.", exception)
            exception.printStackTrace()
            false
        }
    }

}
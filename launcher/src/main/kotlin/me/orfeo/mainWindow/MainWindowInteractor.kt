package me.orfeo.mainWindow

import me.orfeo.utils.FileManager
import mu.KotlinLogging
import org.update4j.Archive
import org.update4j.Configuration
import java.io.IOException
import java.net.URL
import org.update4j.UpdateOptions
import org.update4j.service.UpdateHandler
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

private val logger = KotlinLogging.logger {}

class MainWindowInteractor(
    private val fileManager: FileManager
) {

    fun getConfiguration(): Configuration? {
        val remoteConfigUrl = URL("https://srv-store3.gofile.io/download/SxUqZi/update4jconfig.xml")
        val localConfigUrl = Paths.get(fileManager.appHomePath.absolutePath, "update4jconfig.xml")
        return try {
            remoteConfigUrl.openStream().use {
                Files.copy(it, localConfigUrl, StandardCopyOption.REPLACE_EXISTING)
            }
            Configuration.read(localConfigUrl.toFile().bufferedReader())
        } catch (e: IOException) {
            logger.error("Unable to download update configuration.", e)
            e.printStackTrace()
            null
        }
    }

    fun performUpdate(config: Configuration, progress: (Float) -> Unit): Boolean {
        val zip = Paths.get(fileManager.appHomePath.absolutePath, "escargot-update.zip")
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

    fun getAppHome(): URI{
        return fileManager.appHomePath.toURI()
    }

}
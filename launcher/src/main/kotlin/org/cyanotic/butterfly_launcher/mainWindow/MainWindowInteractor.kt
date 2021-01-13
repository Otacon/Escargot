package org.cyanotic.butterfly_launcher.mainWindow

import mu.KotlinLogging
import org.cyanotic.butterfly_launcher.utils.FileManager
import org.update4j.Archive
import org.update4j.Configuration
import org.update4j.UpdateOptions
import org.update4j.service.UpdateHandler
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

private val logger = KotlinLogging.logger {}
private const val MAX_DOWNLOAD_ATTEMPTS = 5
private const val SLEEP_TIME_BETWEEN_ATTEMPTS = 2_000L

class MainWindowInteractor(
    private val fileManager: FileManager
) {

    fun getConfiguration(): Configuration? {
        val remoteConfigUrl = URL("https://srv-store3.gofile.io/download/SxUqZi/update4jconfig.xml")
        val localConfigUrl = Paths.get(fileManager.appHomePath.absolutePath, "update4jconfig.xml")
        downloadNewConfiguration(remoteConfigUrl, localConfigUrl)
        return try {
            Configuration.read(localConfigUrl.toFile().bufferedReader())
        } catch (e: IOException) {
            logger.error(e) { "Unable to read update configuration." }
            null
        }
    }

    private fun downloadNewConfiguration(remoteUrl: URL, localUrl: Path) {
        var downloaded = false
        var attempts = 0
        while (!downloaded && attempts < MAX_DOWNLOAD_ATTEMPTS) {
            try {
                attempts++
                logger.info { "Downloading update config from $remoteUrl ($attempts/$MAX_DOWNLOAD_ATTEMPTS)" }
                remoteUrl.openStream().use {
                    Files.copy(it, localUrl, StandardCopyOption.REPLACE_EXISTING)
                }
                downloaded = true
            } catch (e: IOException) {
                logger.error(e) { "Unable to download update configuration." }
                logger.info { "Retrying in ${SLEEP_TIME_BETWEEN_ATTEMPTS / 1000} seconds." }
                Thread.sleep(SLEEP_TIME_BETWEEN_ATTEMPTS)
            }
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

    fun getAppHome(): URI {
        return fileManager.appHomePath.toURI()
    }

}
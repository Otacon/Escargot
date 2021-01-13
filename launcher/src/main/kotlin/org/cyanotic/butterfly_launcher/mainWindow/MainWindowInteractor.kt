package org.cyanotic.butterfly_launcher.mainWindow

import mu.KotlinLogging
import org.cyanotic.butterfly_launcher.utils.Endpoints
import org.cyanotic.butterfly_launcher.utils.FileManager
import org.update4j.Archive
import org.update4j.Configuration
import org.update4j.FileMetadata
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
    private val fileManager: FileManager,
    private val endpoints: Endpoints
) {

    fun getConfiguration(): Configuration? {
        val remoteConfigUrl = URL(endpoints.updateConfig)
        val localConfigUrl = Paths.get(fileManager.updateConfigFile.absolutePath)
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
        val updateHandler = object : UpdateHandler {
            override fun updateDownloadProgress(frac: Float) {
                progress(frac)
            }

            override fun startDownloadFile(file: FileMetadata) {
                logger.info { "Downloading ${file.uri}" }
            }
        }
        val zip = Paths.get(fileManager.updateZip.absolutePath)
        logger.info { "Downloading dependencies into $zip" }
        val exception = config.update(UpdateOptions.archive(zip).updateHandler(updateHandler)).exception
        return if (exception == null) {
            logger.info { "Applying update from $zip" }
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
package org.cyanotic.butterfly_launcher.utils

import java.io.File

private const val PACKAGE = "escargot"

val fileManager by lazy {
    val os = System.getProperty("os.name")
    when {
        os.contains("windows", true) -> FileManagerWindows()
        os.contains("mac", true) -> FileManagerMac()
        os.contains("linux", true) -> FileManagerLinux()
        else -> throw IllegalStateException("Unknown OS.")
    }
}

interface FileManager {

    val appHomePath: File

}

private class FileManagerMac : FileManager {
    private val _appHomePath by lazy {
        val home = File(System.getProperty("user.home"))
        val library = File(home, "Library")
        val applicationSupport = File(library, "ApplicationSupport")
        File(applicationSupport, PACKAGE).apply {
            if (!exists()) {
                mkdir()
            }
        }
    }

    override val appHomePath: File
        get() = _appHomePath

}

private class FileManagerWindows : FileManager {
    private val _appHomePath by lazy {
        val home = File(System.getProperty("user.home"))
        val appData = File(home, "AppData")
        val local = File(appData, "Local")
        File(local, PACKAGE).apply {
            if (!exists()) {
                mkdir()
            }
        }
    }

    override val appHomePath: File
        get() = _appHomePath

}

private class FileManagerLinux : FileManager {
    private val _appHomePath by lazy {
        val home = File(System.getProperty("user.home"))
        File(home, PACKAGE).apply {
            if (!exists()) {
                mkdir()
            }
        }
    }

    override val appHomePath: File
        get() = _appHomePath

}
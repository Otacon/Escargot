package core.fileManager

import java.io.File
import java.lang.IllegalStateException

private const val PACKAGE = "Escargot"

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
        val applicationSupport = File(home, "AppData")
        File(applicationSupport, PACKAGE).apply {
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
        val applicationSupport = File(home, ".$PACKAGE")
        File(applicationSupport, PACKAGE).apply {
            if (!exists()) {
                mkdir()
            }
        }
    }

    override val appHomePath: File
        get() = _appHomePath

}
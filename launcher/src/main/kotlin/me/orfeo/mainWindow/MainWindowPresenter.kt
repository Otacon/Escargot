package me.orfeo.mainWindow

import me.orfeo.utils.FileManager
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.Executors
import javax.swing.SwingUtilities
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class MainWindowPresenter(
    private val view: MainWindowContract.View,
    private val interactor: MainWindowInteractor,
    private val fileManager: FileManager
) : MainWindowContract.Presenter {

    private val executor = Executors.newSingleThreadExecutor()
    private var model = MainWindowModel(
        appHome = "",
        status = "",
        progress = 0,
        error = null,
        isLaunchButtonEnabled = false,
        isUpdateButtonEnabled = false,
        isRemoveDataButtonEnabled = false,
        configuration = null
    )

    override fun onCreate() = executor.execute {
        model = model.copy(
            appHome = fileManager.appHomePath.absolutePath,
            status = "Checking for new versions...",
            progress = -1
        )
        updateUI()
        val configuration = interactor.getConfiguration()

        if (configuration == null) {
            model = model.copy(
                progress = 0,
                error = "Unable to check for updates. Please try again.",
                isRemoveDataButtonEnabled = true
            )
            updateUI()
        } else {
            model = model.copy(
                progress = 0,
                configuration = configuration
            )
            checkForUpdates()
        }
    }

    override fun onLaunchClicked() = executor.execute {
        SwingUtilities.invokeLater { view.close() }
        model.configuration!!.launch()
        exitProcess(-1)
    }

    override fun onUpdateClicked() = executor.execute {
        model = model.copy(
            status = "Updating Escargot...",
            error = null,
            progress = -1,
            isUpdateButtonEnabled = false,
            isLaunchButtonEnabled = false,
            isRemoveDataButtonEnabled = false
        )
        updateUI()
        val success = interactor.performUpdate(model.configuration!!, model.appHome) { progress ->
            model = model.copy(progress = (progress * 100).roundToInt())
            updateUI()
        }
        model = if (success) {
            model.copy(
                status = "Escargot is now up to date!",
                progress = 0,
                isUpdateButtonEnabled = false,
                isLaunchButtonEnabled = true,
                isRemoveDataButtonEnabled = true
            )
        } else {
            model.copy(
                status = "",
                progress = 0,
                error = "Unable to update Escargot. Check your connection or clear data.",
                isUpdateButtonEnabled = true,
                isRemoveDataButtonEnabled = true
            )
        }
        updateUI()
    }

    override fun onOpenFilesClicked() {
        view.openFileManager(File(model.appHome).toURI())
    }

    override fun onWindowFocussed() {
        checkForUpdates()
    }

    private fun checkForUpdates() = executor.execute {
        val configuration = model.configuration!!
        val status = if (configuration.requiresUpdate()) {
            "A new version is available."
        } else {
            "Escargot is up to date."
        }
        model = model.copy(
            status = status,
            isLaunchButtonEnabled = !configuration.requiresUpdate(),
            isUpdateButtonEnabled = configuration.requiresUpdate(),
            isRemoveDataButtonEnabled = true,
            progress = 0,
            configuration = configuration
        )
        updateUI()
    }

    private fun updateUI() = SwingUtilities.invokeLater {
        view.apply {
            setStatus(model.status)
            setProgress(model.progress)
            setError(model.error)
            showError(model.error != null)
            setLaunchButtonEnabled(model.isLaunchButtonEnabled)
            setUpdateButtonEnabled(model.isUpdateButtonEnabled)
            setRemoveDataButtonEnabled(model.isRemoveDataButtonEnabled)
        }
    }

}
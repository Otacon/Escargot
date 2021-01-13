package org.cyanotic.butterfly_launcher.mainWindow

import org.cyanotic.butterfly_launcher.utils.Endpoints
import org.cyanotic.butterfly_launcher.utils.fileManager
import java.awt.*
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import java.net.URI
import java.net.URL
import javax.swing.*


class MainWindowView : MainWindowContract.View {

    private val frame: JFrame = JFrame("Escargot Launcher")
    private val statusLabel: JLabel
    private val progressBar: JProgressBar
    private val errorLabel: JLabel
    private val launchButton: JButton
    private val updateButton: JButton
    private val checkForUpdatesButton: JButton
    private val openDataFolderButton: JButton

    private val presenter: MainWindowContract.Presenter = MainWindowPresenter(
        this,
        MainWindowInteractor(fileManager,Endpoints)
    )

    fun show() {
        frame.isVisible = true
        presenter.onCreate()
    }

    override fun setStatus(status: String) {
        statusLabel.text = status
    }

    override fun setProgress(progress: Int) {
        if (progress >= 0) {
            progressBar.isIndeterminate = false
            progressBar.value = progress
        } else {
            progressBar.isIndeterminate = true
        }
    }

    override fun setError(error: String?) {
        errorLabel.text = error
    }

    override fun showError(show: Boolean) {
        errorLabel.isVisible = show
    }

    override fun setLaunchButtonEnabled(enabled: Boolean) {
        launchButton.isEnabled = enabled
    }

    override fun setUpdateButtonEnabled(enabled: Boolean) {
        updateButton.isEnabled = enabled
    }

    override fun setRemoveDataButtonEnabled(enabled: Boolean) {
        openDataFolderButton.isEnabled = enabled
    }

    override fun setCheckForUpdatesButtonEnabled(enabled: Boolean) {
        checkForUpdatesButton.isEnabled = enabled
    }

    override fun close() {
        frame.isVisible = false
    }

    override fun openFileManager(appHome: URI) {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            desktop.browse(appHome)
        }
    }

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = BorderLayout()

        val parentPanel = JPanel()
        parentPanel.layout = GridBagLayout()
        parentPanel.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        frame.contentPane = parentPanel

        statusLabel = JLabel("Status", SwingConstants.CENTER)

        progressBar = JProgressBar(0, 100)
        progressBar.minimumSize = Dimension(300, progressBar.minimumSize.height)
        progressBar.preferredSize = progressBar.minimumSize

        errorLabel = JLabel("Error", SwingConstants.CENTER)
        errorLabel.alignmentX = JLabel.CENTER_ALIGNMENT
        errorLabel.isVisible = false

        launchButton = JButton("Launch")
        launchButton.isEnabled = false
        launchButton.addActionListener {
            presenter.onLaunchClicked()
        }

        updateButton = JButton("Update")
        updateButton.isEnabled = false
        updateButton.addActionListener {
            presenter.onUpdateClicked()
        }

        checkForUpdatesButton = JButton("Check for Updates")
        checkForUpdatesButton.isEnabled = false
        checkForUpdatesButton.addActionListener {
            presenter.onCheckForUpdatesClicked()
        }

        openDataFolderButton = JButton("Open Escargot Folder")
        openDataFolderButton.isEnabled = false
        openDataFolderButton.addActionListener {
            presenter.onOpenFilesClicked()
        }

        parentPanel.add(statusLabel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 0
            insets = Insets(10, 0, 10, 0)
        })
        parentPanel.add(progressBar, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 1
            insets = Insets(10, 0, 10, 0)
        })
        parentPanel.add(errorLabel, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 2
            insets = Insets(10, 0, 10, 0)
        })
        parentPanel.add(checkForUpdatesButton, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 3
            insets = Insets(5, 20, 5, 20)
        })
        parentPanel.add(updateButton, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 4
            insets = Insets(5, 20, 5, 20)
        })
        parentPanel.add(openDataFolderButton, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 5
            insets = Insets(5, 20, 5, 20)
        })
        parentPanel.add(launchButton, GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            gridx = 0
            gridy = 6
            insets = Insets(15, 10, 10, 10)
        })

        frame.pack()
        frame.isResizable = false
        frame.setLocationRelativeTo(null)
        val iconURL: URL = javaClass.getResource("/e-logo.png")
        val icon = ImageIcon(iconURL)
        frame.iconImage = icon.image

        frame.addWindowFocusListener(object : WindowFocusListener {

            override fun windowLostFocus(e: WindowEvent?) {}

            override fun windowGainedFocus(e: WindowEvent?) {
                presenter.onWindowFocussed()
            }
        })
    }

}
package me.orfeo.mainWindow

import javax.swing.*
import java.awt.Desktop
import java.awt.event.WindowEvent
import java.net.URI
import java.awt.event.WindowFocusListener

class MainWindowView : MainWindowContract.View {

    private val frame: JFrame = JFrame("Escargot Launcher")
    private val statusLabel: JLabel
    private val progressBar: JProgressBar
    private val errorLabel: JLabel
    private val launchButton: JButton
    private val updateButton: JButton
    private val removeDataButton: JButton

    private val presenter: MainWindowContract.Presenter = MainWindowPresenter(this, MainWindowInteractor())

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val parentPanel = JPanel()
        parentPanel.layout = BoxLayout(parentPanel, BoxLayout.Y_AXIS)
        parentPanel.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        frame.contentPane = parentPanel

        statusLabel = JLabel("Status")
        statusLabel.alignmentX = JLabel.CENTER_ALIGNMENT
        parentPanel.add(statusLabel)

        progressBar = JProgressBar(0, 100)
        parentPanel.add(progressBar)

        errorLabel = JLabel("Error")
        errorLabel.alignmentX = JLabel.CENTER_ALIGNMENT
        errorLabel.isVisible = false
        parentPanel.add(errorLabel)

        val buttonsPanel = JPanel()
        buttonsPanel.border = BorderFactory.createEmptyBorder(15, 0, 0, 0)
        buttonsPanel.layout = BoxLayout(buttonsPanel, BoxLayout.X_AXIS)

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

        removeDataButton = JButton("Open Escargot Folder")
        removeDataButton.isEnabled = false
        removeDataButton.addActionListener {
            presenter.onOpenFilesClicked()
        }

        buttonsPanel.add(launchButton)
        buttonsPanel.add(updateButton)
        buttonsPanel.add(removeDataButton)

        parentPanel.add(buttonsPanel)

        frame.pack()
        frame.isResizable = false
        frame.setLocationRelativeTo(null)

        frame.addWindowFocusListener(object : WindowFocusListener {

            override fun windowLostFocus(e: WindowEvent?) {}

            override fun windowGainedFocus(e: WindowEvent?) {
                presenter.onWindowFocussed()
            }
        })
    }

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
        removeDataButton.isEnabled = enabled
    }

    override fun close() {
        frame.isVisible = false
    }

    override fun openFileManager(appHome: String) {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            desktop.browse(URI("file:$appHome"))
        }
    }

}
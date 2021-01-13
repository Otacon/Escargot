package org.cyanotic.butterfly.features.loginLoading

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.cyanotic.butterfly.core.AccountManager
import org.cyanotic.butterfly.core.ContactManager
import org.cyanotic.butterfly.core.auth.AuthenticationResult

class LoginLoadingView(
    private val stage: Stage,
    private val username: String,
    private val password: String
) : LoginLoadingContract.View {

    @FXML
    private lateinit var progressBar: ProgressBar

    @FXML
    private lateinit var progressText: Text

    @FXML
    private lateinit var retryButton: Button

    @FXML
    private lateinit var cancelButton: Button

    @FXML
    private lateinit var okButton: Button

    var success: AuthenticationResult.Success? = null

    private val presenter = LoginLoadingPresenter(
        this,
        LoginLoadingInteractor(AccountManager, ContactManager)
    )

    fun onCreate() {
        setupListeners()
        presenter.start(username = username, password = password)
    }

    override fun setProgressText(text: String) {
        progressText.text = text
    }

    override fun showCancel(isVisible: Boolean) {
        cancelButton.isVisible = isVisible
    }

    override fun showOk(isVisible: Boolean) {
        okButton.isVisible = isVisible
    }

    override fun showRetry(isVisible: Boolean) {
        retryButton.isVisible = isVisible
    }

    override fun showProgress(isVisible: Boolean) {
        progressBar.isVisible = isVisible
    }

    override fun closeWithFailure() {
        success = null
        stage.close()
    }

    override fun closeWithSuccess(authData: AuthenticationResult.Success) {
        success = authData
        stage.close()
    }

    private fun setupListeners() {
        cancelButton.setOnMouseClicked { presenter.onCancelClicked() }
        okButton.setOnMouseClicked { presenter.onOkClicked() }
        retryButton.setOnMouseClicked { presenter.onRetryClicked() }
    }

    companion object {
        fun launch(stage: Stage, username: String, password: String): AuthenticationResult.Success? {
            val dialog = Stage(StageStyle.UTILITY)
            val controller = LoginLoadingView(dialog, username, password)
            val root = FXMLLoader().apply {
                setController(controller)
                location = javaClass.getResource("/LoginLoading.fxml")
            }.load<Scene>()
            controller.onCreate()
            dialog.scene = root
            dialog.isResizable = false
            dialog.initOwner(stage)
            dialog.initModality(Modality.APPLICATION_MODAL)
            dialog.showAndWait()
            return controller.success
        }
    }

}
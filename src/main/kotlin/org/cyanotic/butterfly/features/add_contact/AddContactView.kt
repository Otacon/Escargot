package org.cyanotic.butterfly.features.add_contact

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.cyanotic.butterfly.core.ButterflyClient

class AddContactView(
    private val stage: Stage
) : AddContactContract.View {

    @FXML
    private lateinit var formLayout: Pane

    @FXML
    private lateinit var passportTextForm: TextField

    @FXML
    private lateinit var cancelButtonForm: Button

    @FXML
    private lateinit var okButtonForm: Button

    @FXML
    private lateinit var loadingLayout: Pane

    @FXML
    private lateinit var cancelButtonLoading: Button

    @FXML
    private lateinit var successLayout: Pane

    @FXML
    private lateinit var okButtonSuccess: Button

    private val presenter = AddContactPresenter(
        this,
        AddContactInteractor(ButterflyClient)
    )

    private var result : AddContactResult = AddContactResult.Canceled

    fun onCreate() {
        setupListeners()
        presenter.onCreate()
    }

    override fun showForm() {
        formLayout.isVisible = true
        loadingLayout.isVisible = false
        successLayout.isVisible = false
    }

    override fun showLoading() {
        formLayout.isVisible = false
        loadingLayout.isVisible = true
        successLayout.isVisible = false
    }

    override fun showSuccess() {
        formLayout.isVisible = false
        loadingLayout.isVisible = false
        successLayout.isVisible = true
    }

    override fun setAddContactButtonEnabled(enabled: Boolean) {
        okButtonForm.isDisable = enabled.not()
    }

    override fun closeWithSuccess() {
        result = AddContactResult.Added
        stage.close()
    }

    override fun closeWithCancel() {
        result = AddContactResult.Canceled
        stage.close()
    }

    private fun setupListeners() {
        cancelButtonForm.setOnMouseClicked { presenter.onCancelClicked() }
        okButtonForm.setOnMouseClicked { presenter.onAddContactClicked() }
        okButtonSuccess.setOnMouseClicked { presenter.onOkSuccessClicked() }
        cancelButtonLoading.setOnMouseClicked { presenter.onCancelLoadingClicked() }
        passportTextForm.textProperty().addListener { _, old, new ->
            if (old != new) {
                presenter.onPassportChanged(new)
            }
        }
    }

    companion object {
        fun launch(stage: Stage) : AddContactResult {
            val dialog = Stage(StageStyle.UTILITY)
            val controller = AddContactView(dialog)
            val root = FXMLLoader().apply {
                setController(controller)
                location = AddContactView::class.java.getResource("Add_contact.fxml")
            }.load<Scene>()
            controller.onCreate()
            dialog.title = "Add new contact"
            dialog.scene = root
            dialog.isResizable = false
            dialog.initOwner(stage)
            dialog.initModality(Modality.APPLICATION_MODAL)
            dialog.showAndWait()
            return controller.result
        }
    }

}
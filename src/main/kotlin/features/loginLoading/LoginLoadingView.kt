package features.loginLoading

import features.contactList.ContactListView
import features.login.LoginView
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.stage.Stage

class LoginLoadingView(
    private val stage: Stage,
    username: String,
    password: String
) : LoginLoadingContract.View {

    private lateinit var buttonCancel: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: Label
    private val presenter = LoginLoadingPresenter(
        this,
        core.ProfileManager
    )

    init {
        val resource = javaClass.getResource("/LoginLoading.fxml")
        val root = FXMLLoader.load<Scene>(resource)
        stage.scene = root
        stage.isResizable = false
        bindViews(root)
        setupListeners()
        stage.show()
        presenter.start(username, password)
    }

    private fun bindViews(root: Scene) {
        progressBar = root.lookup("#progress") as ProgressBar
        buttonCancel = root.lookup("#cancel") as Button
        progressText = root.lookup("#text") as Label
    }

    private fun setupListeners() {
        buttonCancel.setOnMouseClicked { presenter.onCancelClicked() }
    }


    override fun setProgress(text: String) {
        progressText.text = text
    }

    override fun goToLogin() {
        LoginView(stage)
    }

    override fun goToContactList() {
        ContactListView(stage)
    }

}
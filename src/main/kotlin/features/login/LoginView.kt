package features.login

import features.loginLoading.LoginLoadingView
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.stage.Stage

class LoginView(
    private val stage: Stage
) : LoginContract.View {
    private lateinit var textUsername: TextField
    private lateinit var textPassword: PasswordField
    private lateinit var checkRememberUserProfile: CheckBox
    private lateinit var checkRememberPassword: CheckBox
    private lateinit var checkAccessAutomatically: CheckBox
    private lateinit var buttonLogin: Button
    private lateinit var progressBar: ProgressBar
    private val presenter = LoginPresenter(this)

    init {
        val resource = javaClass.getResource("/Login.fxml")
        val root = FXMLLoader.load<Scene>(resource)

        stage.title = "Escargot 0.1 (In-Dev)"
        stage.scene = root
        stage.isResizable = false
        stage.icons.add(Image(javaClass.getResourceAsStream("/e-logo.png")))
        bindViews(root)
        setupListeners()

        stage.show()
    }

    private fun bindViews(root: Scene) {
        textUsername = root.lookup("#username") as TextField
        textPassword = root.lookup("#password") as PasswordField
        checkRememberUserProfile = root.lookup("#check_remember_user_profile") as CheckBox
        checkRememberPassword = root.lookup("#check_remember_password") as CheckBox
        checkAccessAutomatically = root.lookup("#check_access_automatically") as CheckBox
        buttonLogin = root.lookup("#button_login") as Button
    }

    private fun setupListeners() {
        buttonLogin.setOnMouseClicked { presenter.onLoginClicked() }
        textUsername.textProperty().addListener { _, old, new ->
            if (old != new) {
                presenter.onUsernameChanged(new)
            }
        }
        textPassword.textProperty().addListener { _, old, new ->
            if (old != new) {
                presenter.onPasswordChanged(new)
            }
        }
        checkRememberUserProfile.selectedProperty()
            .addListener { _, _, checked -> presenter.onRememberProfileChecked(checked) }
        checkRememberPassword.selectedProperty()
            .addListener { _, _, checked -> presenter.onRememberPasswordChecked(checked) }
        checkAccessAutomatically.selectedProperty()
            .addListener { _, _, checked -> presenter.onAccessAutomaticallyChecked(checked) }
    }

    override fun setUsername(username: String) {
        textUsername.text = username
    }

    override fun setPassword(password: String) {
        textPassword.text = password
    }

    override fun setRememberUserProfileChecked(isChecked: Boolean) {
        checkRememberUserProfile.isSelected = isChecked
    }

    override fun setRememberPasswordChecked(isChecked: Boolean) {
        checkRememberPassword.isSelected = isChecked
    }

    override fun setAccessAutomatically(isChecked: Boolean) {
        checkAccessAutomatically.isSelected = isChecked
    }

    override fun setLoginEnabled(loginEnabled: Boolean) {
        buttonLogin.isDisable = !loginEnabled
    }

    override fun goToLoading(username: String, password: String) {
        LoginLoadingView(stage, username, password)
    }

    override fun setProgress(progress: Double) {
        progressBar.progress = progress
    }


}
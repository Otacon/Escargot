package features.login

import features.appInstance
import features.contactList.ContactListView
import features.loginLoading.LoginLoadingView
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.stage.Stage

class LoginView(
    private val stage: Stage
) : LoginContract.View {

    @FXML
    private lateinit var textUsername: TextField

    @FXML
    private lateinit var textPassword: PasswordField

    @FXML
    private lateinit var checkRememberUserProfile: CheckBox

    @FXML
    private lateinit var checkRememberPassword: CheckBox

    @FXML
    private lateinit var checkAccessAutomatically: CheckBox

    @FXML
    private lateinit var buttonLogin: Button

    @FXML
    private lateinit var signupHyperlink: Hyperlink

    private val presenter = LoginPresenter(this)

    fun onCreate(root: Scene) {
        stage.title = "Escargot 0.1 (In-Dev)"
        stage.scene = root
        stage.isResizable = false
        stage.icons.add(Image(javaClass.getResourceAsStream("/e-logo.png")))
        setupListeners()
        stage.show()
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
        signupHyperlink.setOnMouseClicked {
            presenter.onSignupClicked()
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
        LoginLoadingView.launch(stage, username, password)?.let {
            ContactListView.launch(stage, it.passport, it.token)
        }
    }

    override fun openWebBrowser(url: String) {
        appInstance.hostServices.showDocument(url)
    }

    companion object {
        fun launch(stage: Stage) {
            val controller = LoginView(stage)
            val root = FXMLLoader().apply {
                setController(controller)
                location = javaClass.getResource("/Login.fxml")
            }.load<Scene>()
            controller.onCreate(root)
        }
    }

}
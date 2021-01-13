package org.cyanotic.butterfly.features.login

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.stage.Stage
import org.cyanotic.butterfly.core.AccountManager
import org.cyanotic.butterfly.features.appInstance
import org.cyanotic.butterfly.features.contactList.ContactListView
import org.cyanotic.butterfly.features.loginLoading.LoginLoadingView

class LoginView(
    private val stage: Stage
) : LoginContract.View {

    @FXML
    private lateinit var textUsername: ComboBox<String>

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

    private val presenter = LoginPresenter(this, LoginInteractor(AccountManager))

    fun onCreate(root: Scene) {
        stage.title = "Escargot 0.1 (In-Dev)"
        stage.scene = root
        stage.isResizable = false
        stage.icons.add(Image(javaClass.getResourceAsStream("/e-logo.png")))
        setupListeners()
        stage.show()
        textUsername.requestFocus()
        presenter.onStart()
    }

    private fun setupListeners() {
        buttonLogin.setOnMouseClicked { presenter.onLoginClicked() }
        textUsername.editor.textProperty().addListener { _, old, new ->
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
        textUsername.editor.text = username
    }

    override fun setAccountsHistory(usernames: List<String>) {
        textUsername.items.clear()
        textUsername.items.addAll(usernames)
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
            presenter.onLoginSuccessful(it.token)
        }
    }

    override fun goToContactList() {
        ContactListView.launch(stage)
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
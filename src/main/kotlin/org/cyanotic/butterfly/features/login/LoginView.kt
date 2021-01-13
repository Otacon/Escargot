package org.cyanotic.butterfly.features.login

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
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
import org.cyanotic.butterfly.protocol.Status

class LoginView(
    private val stage: Stage
) : LoginContract.View {

    @FXML
    private lateinit var textUsername: ComboBox<String>

    @FXML
    private lateinit var textPassword: PasswordField

    @FXML
    private lateinit var comboStatus: ComboBox<Status>

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

    fun onCreate(root: Scene, autoLogin: Boolean) {
        stage.title = "Escargot 0.1 (In-Dev)"
        stage.scene = root
        stage.isResizable = false
        stage.icons.add(Image(javaClass.getResourceAsStream("/e-logo.png")))
        setupStatusComboBox()
        setupListeners()
        stage.show()
        textUsername.requestFocus()
        presenter.onCreate(autoLogin)
    }

    private fun setupStatusComboBox(){
        comboStatus.cellFactory = javafx.util.Callback<ListView<Status>, ListCell<Status>> {
            object : ListCell<Status>() {
                override fun updateItem(item: Status?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item == null || empty) {
                        graphic = null
                    } else {
                        val text = when (item) {
                            Status.ONLINE -> "Available"
                            Status.AWAY -> "Away"
                            Status.BE_RIGHT_BACK -> "Be right back"
                            Status.IDLE -> "Idle"
                            Status.OUT_TO_LUNCH -> "Out to lunch"
                            Status.ON_THE_PHONE -> "On the phone"
                            Status.BUSY -> "Busy"
                            Status.OFFLINE -> "Offline"
                            Status.HIDDEN -> "Appear offline"
                        }
                        setText(text)
                    }
                }
            }
        }
        comboStatus.buttonCell = comboStatus.cellFactory.call(null)
        val comboItems = listOf(
            Status.ONLINE,
            Status.AWAY,
            Status.BUSY,
            Status.HIDDEN
        )
        comboStatus.items.addAll(comboItems)
        comboStatus.selectionModel.select(comboItems.first())
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

        comboStatus.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            presenter.onLoginStatusChanged(newValue!!)
        }
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

    override fun goToLoading(username: String, password: String, status: Status) {
        val result = LoginLoadingView.launch(stage, username, password, status)
        presenter.onLoginResult(result)
    }

    override fun goToContactList() {
        ContactListView.launch(stage)
    }

    override fun openWebBrowser(url: String) {
        appInstance.hostServices.showDocument(url)
    }

    companion object {
        fun launch(stage: Stage, autoLogin: Boolean) {
            val controller = LoginView(stage)
            val root = FXMLLoader().apply {
                setController(controller)
                location = LoginView::class.java.getResource("Login.fxml")
            }.load<Scene>()
            controller.onCreate(root, autoLogin)
        }
    }

}
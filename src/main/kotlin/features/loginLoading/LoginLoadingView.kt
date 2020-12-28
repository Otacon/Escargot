package features.loginLoading

import features.contactList.ContactListView
import features.login.LoginView
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.stage.Stage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import protocol.authentication.Authenticator
import protocol.authentication.RequestMultipleSecurityTokensRequestFactory
import protocol.notification.NotificationTransportManager
import protocol.security.TicketEncoder
import protocol.soap.RequestSecurityTokenParser
import protocol.utils.SystemInfoRetrieverDesktop

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
        Authenticator(
            SystemInfoRetrieverDesktop(),
            NotificationTransportManager.transport,
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY }).build(),
            TicketEncoder(),
            RequestMultipleSecurityTokensRequestFactory(),
            RequestSecurityTokenParser()
        )
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
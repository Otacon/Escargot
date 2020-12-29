package features.loginLoading

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import protocol.authentication.AuthenticationResult
import protocol.authentication.Authenticator
import kotlin.coroutines.CoroutineContext

class LoginLoadingPresenter constructor(
    private val view: LoginLoadingContract.View,
    private val authenticator: Authenticator
) : LoginLoadingContract.Presenter, CoroutineScope {

    private var model = LoginLoadingModel(
        username = "",
        password = "",
        text = "Loading..."
    )

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start(username: String, password: String) {
        model = model.copy(username = username, password = password, text = "Protocol handshake...")
        updateUI()
        launch(Dispatchers.IO) {
            val result = authenticator.authenticate(username, password)
            launch(Dispatchers.JavaFx) {
                when (result) {
                    AuthenticationResult.UnsupportedProtocol,
                    AuthenticationResult.InvalidPassword,
                    AuthenticationResult.ServerError -> view.goToLogin()
                    AuthenticationResult.Success -> view.goToContactList()

                }
            }
        }
    }

    override fun onCancelClicked() {
        view.goToLogin()
    }

    private fun updateUI() {
        view.setProgress(model.text)
    }


}
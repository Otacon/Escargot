package features.loginLoading

import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import usecases.Login
import usecases.LoginResult
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime

class LoginLoadingPresenter constructor(
    private val view: LoginLoadingContract.View,
    private val login: Login
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
            val result =login(username, password)
            launch(Dispatchers.JavaFx) {
                when (result) {
                    LoginResult.Success -> view.goToContactList()
                    LoginResult.Failure -> view.goToLogin()
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
package features.loginLoading

import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlin.coroutines.CoroutineContext

class LoginLoadingPresenter(
    private val view: LoginLoadingContract.View
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
            delay(2000)
            launch(Dispatchers.JavaFx) {
                model = model.copy(text = "Sending client specification...")
                updateUI()
            }
            delay(2000)
            launch(Dispatchers.JavaFx) {
                model = model.copy(text = "Authenticating...")
                updateUI()
            }
            delay(2000)
            launch(Dispatchers.JavaFx) {
                view.goToContactList()
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
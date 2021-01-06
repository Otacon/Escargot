package features.loginLoading

import core.AuthenticationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import protocol.Status
import kotlin.coroutines.CoroutineContext

class LoginLoadingPresenter constructor(
    private val view: LoginLoadingContract.View,
    private val interactor: LoginLoadingInteractor
) : LoginLoadingContract.Presenter, CoroutineScope {

    private var model = LoginLoadingModel(
        username = "",
        password = "",
        text = "Authenticating...",
        okVisible = false,
        cancelVisible = true,
        retryVisible = false,
        progressVisible = true
    )

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun start(username: String, password: String) {
        model = model.copy(username = username, password = password)
        startAuthentication()
    }

    override fun onCancelClicked() {
        job.cancel()
        view.closeWithFailure()
    }

    override fun onOkClicked() {
        view.closeWithFailure()
    }

    override fun onRetryClicked() {
        startAuthentication()
    }

    private fun startAuthentication() {
        model = model.copy(
            text = "Authenticating...",
            okVisible = false,
            cancelVisible = true,
            retryVisible = false,
            progressVisible = true
        )
        updateUI()
        launch(Dispatchers.IO) {
            val result = interactor.login(model.username, model.password)
            launch(Dispatchers.JavaFx) {
                model = when (result) {
                    AuthenticationResult.UnsupportedProtocol -> model.copy(
                        text = "This application is incompatible with the server. Please consider updating.",
                        okVisible = true,
                        cancelVisible = false,
                        retryVisible = false,
                        progressVisible = false
                    )
                    AuthenticationResult.InvalidPassword -> model.copy(
                        text = "Invalid password. Please check your details and try again",
                        okVisible = true,
                        cancelVisible = false,
                        retryVisible = false,
                        progressVisible = false
                    )
                    AuthenticationResult.InvalidUser -> model.copy(
                        text = "Invalid user. Please check your details and try again",
                        okVisible = true,
                        cancelVisible = false,
                        retryVisible = false,
                        progressVisible = false
                    )
                    AuthenticationResult.ServerError -> model.copy(
                        text = "Whoops...something went wrong. Please try again.",
                        okVisible = false,
                        cancelVisible = true,
                        retryVisible = true,
                        progressVisible = false
                    )
                    is AuthenticationResult.Success -> {
                        model = model.copy(text = "Syncing contact list...")
                        updateUI()
                        interactor.refreshContactList()
                        model = model.copy(text = "Updating status...")
                        updateUI()
                        interactor.updateStatus(Status.ONLINE)
                        view.closeWithSuccess(result)
                        return@launch
                    }
                }
                updateUI()
            }
        }
    }

    private fun updateUI() {
        view.setProgressText(model.text)
        view.showProgress(model.progressVisible)
        view.showCancel(model.cancelVisible)
        view.showOk(model.okVisible)
        view.showRetry(model.retryVisible)
    }


}
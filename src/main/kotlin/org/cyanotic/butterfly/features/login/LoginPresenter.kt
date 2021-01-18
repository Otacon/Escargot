package org.cyanotic.butterfly.features.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.cyanotic.butterfly.features.login_loading.LoginResult
import org.cyanotic.butterfly.protocol.Status
import kotlin.coroutines.CoroutineContext

class LoginPresenter(
    private val view: LoginContract.View,
    private val interactor: LoginInteractor
) : LoginContract.Presenter, CoroutineScope {

    private var model = LoginModel(
        username = "",
        password = "",
        rememberProfile = false,
        rememberPassword = false,
        accessAutomatically = false,
        isLoginEnabled = true,
        loginStatus = Status.ONLINE
    )

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(autoLogin: Boolean) {
        launch(Dispatchers.IO) {
            val latestUsedAccounts = interactor.getSavedUsernames()
            val latestAccount = interactor.getLastUsedAccount()
            latestAccount?.let {
                model = model.copy(
                    username = latestAccount.passport,
                    password = latestAccount.password ?: "",
                    rememberProfile = latestAccount.temporary.not(),
                    rememberPassword = latestAccount.password != null,
                    accessAutomatically = latestAccount.auto_sigin
                )
            }

            launch(Dispatchers.JavaFx) {
                view.setAccountsHistory(latestUsedAccounts)
                updateUI()
                if (latestAccount?.auto_sigin == true && autoLogin) {
                    view.goToLoading(model.username, model.password, model.loginStatus)
                }
            }
        }
    }

    override fun onUsernameChanged(username: String) {
        launch(Dispatchers.IO) {
            val account = interactor.getAccountByPassport(username)
            if (account != null) {
                model = model.copy(
                    username = account.passport,
                    password = account.password ?: "",
                    rememberProfile = account.temporary.not(),
                    rememberPassword = account.password != null,
                    accessAutomatically = account.auto_sigin
                )
                launch(Dispatchers.JavaFx) {
                    updateUI()
                }
            } else {
                model = model.copy(username = username, isLoginEnabled = isLoginEnabled(username, model.password))
            }

        }
    }

    override fun onPasswordChanged(password: String) {
        model = model.copy(password = password, isLoginEnabled = isLoginEnabled(model.username, password))
        updateUI()
    }

    override fun onLoginStatusChanged(loginStatus: Status) {
        model = model.copy(loginStatus = loginStatus)
    }

    override fun onRememberProfileChecked(isChecked: Boolean) {
        model = model.copy(rememberProfile = isChecked)
        updateUI()
    }

    override fun onRememberPasswordChecked(isChecked: Boolean) {
        model = model.copy(rememberPassword = isChecked)
        updateUI()
    }

    override fun onAccessAutomaticallyChecked(isChecked: Boolean) {
        model = model.copy(accessAutomatically = isChecked)
        updateUI()
    }

    override fun onLoginClicked() {
        view.goToLoading(model.username, model.password, model.loginStatus)
    }

    override fun onSignupClicked() {
        view.openWebBrowser("https://escargot.log1p.xyz/register")
    }

    override fun onLoginResult(result: LoginResult) {
        launch(Dispatchers.IO) {
            interactor.updateLoginPreferences(
                savePassword = model.rememberPassword,
                password = model.password,
                rememberUser = model.rememberProfile,
                autoSignin = model.accessAutomatically
            )
            launch(Dispatchers.JavaFx) {
                view.goToContactList()
            }
        }
    }

    private fun isLoginEnabled(username: String, password: String): Boolean {
        return username.isNotBlank() && password.isNotBlank()
    }

    private fun updateUI() {
        view.setUsername(model.username)
        view.setPassword(model.password)
        view.setRememberUserProfileChecked(model.rememberProfile)
        view.setRememberPasswordChecked(model.rememberPassword)
        view.setAccessAutomatically(model.accessAutomatically)
        view.setLoginEnabled(model.isLoginEnabled)
    }
}
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
                    password = latestAccount.password,
                    rememberProfile = latestAccount.rememberPassport,
                    rememberPassword = latestAccount.rememberPassword,
                    accessAutomatically = latestAccount.loginAutomatically
                )
            }

            launch(Dispatchers.JavaFx) {
                view.setAccountsHistory(latestUsedAccounts)
                updateUI()
                if (latestAccount?.loginAutomatically == true && autoLogin) {
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
                    password = account.password,
                    rememberProfile = account.rememberPassport,
                    rememberPassword = account.rememberPassword,
                    accessAutomatically = account.loginAutomatically
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
        model = if (isChecked) {
            model.copy(rememberProfile = isChecked)
        } else {
            model.copy(rememberProfile = false, rememberPassword = false, accessAutomatically = false)
        }
        updateUI()
    }

    override fun onRememberPasswordChecked(isChecked: Boolean) {
        model = if (isChecked) {
            model.copy(rememberPassword = true, rememberProfile = true)
        } else {
            model.copy(rememberPassword = false, accessAutomatically = false)
        }
        updateUI()
    }

    override fun onAccessAutomaticallyChecked(isChecked: Boolean) {
        model = if (isChecked) {
            model.copy(rememberPassword = true, rememberProfile = true, accessAutomatically = true)
        } else {
            model.copy(accessAutomatically = false)
        }
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
            when (result) {
                is LoginResult.Success -> {
                    if (model.rememberProfile) {
                        interactor.updateLoginPreferences(
                            passport = model.username,
                            password = model.password,
                            savePassword = model.rememberPassword,
                            rememberUser = model.rememberProfile,
                            autoSignin = model.accessAutomatically
                        )
                    }
                    launch(Dispatchers.JavaFx) {
                        view.goToContactList()
                    }
                }
                LoginResult.Failed,
                LoginResult.Canceled -> {
                }
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
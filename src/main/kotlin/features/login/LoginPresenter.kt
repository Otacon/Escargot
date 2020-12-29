package features.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class LoginPresenter(
    private val view: LoginContract.View
) : LoginContract.Presenter, CoroutineScope {

    private var model = LoginModel(
        username = "",
        password = "",
        rememberProfile = false,
        rememberPassword = false,
        accessAutomatically = false,
        isLoginEnabled = true
    )

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onStart() {

    }

    override fun onUsernameChanged(username: String) {
        model = model.copy(username = username, isLoginEnabled = isLoginEnabled(username, model.password))
        updateUI()
    }

    override fun onPasswordChanged(password: String) {
        model = model.copy(password = password, isLoginEnabled = isLoginEnabled(model.username, password))
        updateUI()
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
        view.goToLoading(model.username, model.password)
    }

    override fun onSignupClicked() {
        view.openWebBrowser("https://escargot.log1p.xyz/register")
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
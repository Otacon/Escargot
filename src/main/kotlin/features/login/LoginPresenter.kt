package features.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import repositories.profile.ProfileRepository
import kotlin.coroutines.CoroutineContext

class LoginPresenter(
    private val view: LoginContract.View,
    private val profileRepository: ProfileRepository
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
        launch(Dispatchers.IO) {
            val latestUsedAccounts = profileRepository.getLatestUsedAccounts()
            val savedUsernames = latestUsedAccounts.map { it.passport }
            val latestAccount = latestUsedAccounts.firstOrNull()
            model = model.copy(
                username = latestAccount?.passport ?: model.username,
                password = latestAccount?.password ?: "",
                rememberProfile = latestAccount?.temporary?.not() ?: model.rememberProfile,
                rememberPassword = latestAccount?.password != null,
                accessAutomatically = latestAccount?.auto_sigin ?: model.accessAutomatically
            )
            launch(Dispatchers.JavaFx) {
                view.setUsernameOptions(savedUsernames)
                updateUI()
                if (latestAccount?.auto_sigin == true) {
                    view.goToLoading(model.username, model.password)
                }
            }
        }
    }

    override fun onUsernameChanged(username: String) {
        launch(Dispatchers.IO) {
            val account = profileRepository.getAccountByPassport(username)
            model = if (account != null) {
                model.copy(
                    username = account.passport,
                    password = account.password ?: "",
                    rememberProfile = account.temporary.not(),
                    rememberPassword = account.password != null,
                    accessAutomatically = account.auto_sigin
                )
            } else {
                model.copy(username = username, isLoginEnabled = isLoginEnabled(username, model.password))
            }
            launch(Dispatchers.JavaFx) {
                updateUI()
            }
        }
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

    override fun onLoginSuccessful(mspAuth: String) {
        launch(Dispatchers.IO) {
            profileRepository.saveAccount(
                username = model.username,
                password = model.password,
                mspAuth = mspAuth,
                rememberUser = model.rememberProfile,
                rememberPassword = model.rememberPassword,
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
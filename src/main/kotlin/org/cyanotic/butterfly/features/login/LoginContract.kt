package org.cyanotic.butterfly.features.login

import org.cyanotic.butterfly.features.login_loading.LoginResult
import org.cyanotic.butterfly.protocol.Status

interface LoginContract {

    interface View {

        fun setUsername(username: String)
        fun setAccountsHistory(usernames: List<String>)
        fun setPassword(password: String)
        fun setRememberUserProfileChecked(isChecked: Boolean)
        fun setRememberPasswordChecked(isChecked: Boolean)
        fun setAccessAutomatically(isChecked: Boolean)
        fun setLoginEnabled(loginEnabled: Boolean)
        fun goToLoading(username: String, password: String, status: Status)
        fun openWebBrowser(url: String)
        fun goToContactList()
    }

    interface Presenter {

        fun onCreate(autoLogin: Boolean)
        fun onUsernameChanged(username: String)
        fun onPasswordChanged(password: String)
        fun onLoginStatusChanged(loginStatus: Status)
        fun onRememberProfileChecked(isChecked: Boolean)
        fun onRememberPasswordChecked(isChecked: Boolean)
        fun onAccessAutomaticallyChecked(isChecked: Boolean)
        fun onLoginClicked()
        fun onSignupClicked()
        fun onLoginResult(result: LoginResult)

    }

}
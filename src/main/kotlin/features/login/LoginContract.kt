package features.login

interface LoginContract {

    interface View {

        fun setUsername(username: String)
        fun setAccountsHistory(usernames: List<String>)
        fun setPassword(password: String)
        fun setRememberUserProfileChecked(isChecked: Boolean)
        fun setRememberPasswordChecked(isChecked: Boolean)
        fun setAccessAutomatically(isChecked: Boolean)
        fun setLoginEnabled(loginEnabled: Boolean)
        fun goToLoading(username: String, password: String)
        fun openWebBrowser(url: String)
        fun goToContactList()
    }

    interface Presenter {

        fun onStart()
        fun onUsernameChanged(username: String)
        fun onPasswordChanged(password: String)
        fun onRememberProfileChecked(isChecked: Boolean)
        fun onRememberPasswordChecked(isChecked: Boolean)
        fun onAccessAutomaticallyChecked(isChecked: Boolean)
        fun onLoginClicked()
        fun onSignupClicked()
        fun onLoginSuccessful(mspAuth: String)

    }

}
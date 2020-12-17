package features.login

interface LoginContract {

    interface View {

        fun setUsername(username: String)
        fun setPassword(password: String)
        fun setRememberUserProfileChecked(isChecked: Boolean)
        fun setRememberPasswordChecked(isChecked: Boolean)
        fun setAccessAutomatically(isChecked: Boolean)
        fun setLoginEnabled(loginEnabled: Boolean)
        fun setProgress(progress: Double)
        fun goToLoading(username: String, password: String)

    }

    interface Presenter {

        fun onStart()
        fun onUsernameChanged(username: String)
        fun onPasswordChanged(password: String)
        fun onRememberProfileChecked(isChecked: Boolean)
        fun onRememberPasswordChecked(isChecked: Boolean)
        fun onAccessAutomaticallyChecked(isChecked: Boolean)
        fun onLoginClicked()

    }

}
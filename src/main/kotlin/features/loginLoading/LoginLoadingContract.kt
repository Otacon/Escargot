package features.loginLoading

interface LoginLoadingContract {

    interface View {

        fun setProgress(text: String)
        fun goToLogin()
        fun goToContactList()

    }

    interface Presenter {

        fun start(username: String, password: String)
        fun onCancelClicked()

    }

}


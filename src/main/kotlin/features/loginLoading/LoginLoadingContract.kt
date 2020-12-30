package features.loginLoading

interface LoginLoadingContract {

    interface View {

        fun setProgressText(text: String)
        fun showCancel(isVisible: Boolean)
        fun showOk(isVisible: Boolean)
        fun showRetry(isVisible: Boolean)
        fun showProgress(isVisible: Boolean)
        fun closeWithFailure()
        fun closeWithSuccess()

    }

    interface Presenter {

        fun start(username: String, password: String)
        fun onCancelClicked()
        fun onOkClicked()
        fun onRetryClicked()

    }

}


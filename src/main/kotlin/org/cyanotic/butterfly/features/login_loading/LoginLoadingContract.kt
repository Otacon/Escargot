package org.cyanotic.butterfly.features.login_loading

import org.cyanotic.butterfly.protocol.Status


interface LoginLoadingContract {

    interface View {

        fun setProgressText(text: String)
        fun showCancel(isVisible: Boolean)
        fun showOk(isVisible: Boolean)
        fun showRetry(isVisible: Boolean)
        fun showProgress(isVisible: Boolean)
        fun close(result: LoginResult)

    }

    interface Presenter {

        fun onCreate(username: String, password: String, status: Status)
        fun onCancelClicked()
        fun onOkClicked()
        fun onRetryClicked()

    }

}


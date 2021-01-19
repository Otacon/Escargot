package org.cyanotic.butterfly.features.add_contact

interface AddContactContract {

    interface View {

        fun showForm()
        fun showLoading()
        fun showSuccess()
        fun setAddContactButtonEnabled(enabled: Boolean)
        fun closeWithSuccess()
        fun closeWithCancel()

    }

    interface Presenter {

        fun onCreate()
        fun onCancelClicked()
        fun onAddContactClicked()
        fun onPassportChanged(passport: String)
        fun onOkSuccessClicked()
        fun onCancelLoadingClicked()

    }

}


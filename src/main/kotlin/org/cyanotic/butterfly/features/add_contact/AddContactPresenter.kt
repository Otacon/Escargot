package org.cyanotic.butterfly.features.add_contact

import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlin.coroutines.CoroutineContext

class AddContactPresenter constructor(
    private val view: AddContactContract.View,
    private val interactor: AddContactInteractor
) : AddContactContract.Presenter, CoroutineScope {

    private var model = AddContactModel(
        passport = "",
        isAddContactButtonEnabled = false
    )

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate() {
        updateUI()
    }

    override fun onCancelClicked() {
        view.closeWithCancel()
    }

    override fun onAddContactClicked() {
        view.showLoading()
        launch(Dispatchers.IO){
            interactor.addContact(model.passport)
            launch(Dispatchers.JavaFx){
                view.showSuccess()
            }
        }
    }

    override fun onPassportChanged(passport: String) {
        model = model.copy(
            passport = passport,
            isAddContactButtonEnabled = passport.isNotBlank()
        )
        updateUI()
    }

    override fun onOkSuccessClicked() {
        view.closeWithSuccess()
    }

    override fun onCancelLoadingClicked() {
        view.closeWithCancel()
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setAddContactButtonEnabled(model.isAddContactButtonEnabled)
    }


}
package org.cyanotic.butterfly.features.add_contact

import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlin.coroutines.CoroutineContext

class AddContactPresenter constructor(
    private val view: AddContactContract.View,
) : AddContactContract.Presenter, CoroutineScope {

    private var model = AddContactModel(
        passport = "",
        isAddContactButtonEnabled = false
    )

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate() {

    }

    override fun onCancelClicked() {
        view.close()
    }

    override fun onAddContactClicked() {
        view.showLoading()
        launch(Dispatchers.IO){
            delay(5000)
            launch(Dispatchers.JavaFx){
                view.showSuccess()
            }
        }
    }

    override fun onPassportChanged(passport: String) {
        model = model.copy(passport = passport)
    }

    override fun onOkSuccessClicked() {
        view.close()
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setAddContactButtonEnabled(model.isAddContactButtonEnabled)
    }


}
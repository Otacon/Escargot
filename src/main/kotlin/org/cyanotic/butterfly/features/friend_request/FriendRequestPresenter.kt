package org.cyanotic.butterfly.features.friend_request

import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlin.coroutines.CoroutineContext

class FriendRequestPresenter constructor(
    private val view: FriendRequestContract.View,
    private val interactor: FriendRequestInteractor
) : FriendRequestContract.Presenter, CoroutineScope {

    private var model = FriendRequestModel(
        passport = ""
    )

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(passport: String) {
        model = model.copy(passport = passport)
        updateUI()
    }

    override fun onIgnoreClicked() {
        view.closeWithReject()
    }

    override fun onAcceptClicked() {
        launch(Dispatchers.IO) {
            interactor.addContact(model.passport)
            launch(Dispatchers.JavaFx) {
                view.closeWithAccept()
            }
        }

    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setMessage("${model.passport} would like to start chatting with you.")
    }


}
package org.cyanotic.butterfly.features.friend_request

import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlin.coroutines.CoroutineContext

class FriendRequestPresenter constructor(
    private val view: FriendRequestContract.View,
) : FriendRequestContract.Presenter, CoroutineScope {

    private var model = FriendRequestModel(
        passport = "",
        isAddContactButtonEnabled = false
    )

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate() {

    }

    override fun onIgnoreClicked() {
        view.close()
    }

    override fun onAcceptClicked() {
        view.close()
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
    }


}
package org.cyanotic.butterfly.features.friend_request

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.cyanotic.butterfly.core.ButterflyClient

class FriendRequestView(
    private val stage: Stage
) : FriendRequestContract.View {

    @FXML
    private lateinit var descriptionText: Label

    @FXML
    private lateinit var ignoreButton: Button

    @FXML
    private lateinit var acceptButton: Button

    private val presenter = FriendRequestPresenter(
        this,
        FriendRequestInteractor(ButterflyClient)
    )

    private var result: FriendRequestResult = FriendRequestResult.Rejected

    fun onCreate(passport: String) {
        setupListeners()
        presenter.onCreate(passport)
    }

    override fun closeWithAccept() {
        result = FriendRequestResult.Accepted
        stage.close()
    }

    override fun closeWithReject() {
        result = FriendRequestResult.Rejected
        stage.close()
    }

    override fun setMessage(message: String) {
        descriptionText.text = message
    }

    private fun setupListeners() {
        ignoreButton.setOnMouseClicked { presenter.onIgnoreClicked() }
        acceptButton.setOnMouseClicked { presenter.onAcceptClicked() }
    }

    companion object {
        fun launch(stage: Stage, passport: String): FriendRequestResult {
            val dialog = Stage(StageStyle.UTILITY)
            val controller = FriendRequestView(dialog)
            val root = FXMLLoader().apply {
                setController(controller)
                location = FriendRequestView::class.java.getResource("Friend_request.fxml")
            }.load<Scene>()
            controller.onCreate(passport)
            dialog.title = "New friend request"
            dialog.scene = root
            dialog.isResizable = false
            dialog.initOwner(stage)
            dialog.initModality(Modality.APPLICATION_MODAL)
            dialog.showAndWait()
            return controller.result
        }
    }

}
package org.cyanotic.butterfly.features.friend_request

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

class FriendRequestView(
    private val stage: Stage
) : FriendRequestContract.View {

    @FXML
    private lateinit var text: Text

    @FXML
    private lateinit var ignoreButton: Button

    @FXML
    private lateinit var acceptButton: Button

    private val presenter = FriendRequestPresenter(
        this
    )

    fun onCreate() {
        setupListeners()
        presenter.onCreate()
    }

    override fun close() {
        stage.close()
    }

    private fun setupListeners() {
        ignoreButton.setOnMouseClicked { presenter.onIgnoreClicked() }
        acceptButton.setOnMouseClicked { presenter.onAcceptClicked() }
    }

    companion object {
        fun launch(stage: Stage) {
            val dialog = Stage(StageStyle.UTILITY)
            val controller = FriendRequestView(dialog)
            val root = FXMLLoader().apply {
                setController(controller)
                location = FriendRequestView::class.java.getResource("Friend_request.fxml")
            }.load<Scene>()
            controller.onCreate()
            dialog.title = "New friend request"
            dialog.scene = root
            dialog.isResizable = false
            dialog.initOwner(stage)
            dialog.initModality(Modality.APPLICATION_MODAL)
            dialog.show()
        }
    }

}
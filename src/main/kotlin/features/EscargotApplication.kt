package features

import features.login.LoginView
import javafx.application.Application
import javafx.stage.Stage

class EscargotApplication: Application() {

    override fun start(primaryStage: Stage) {
        LoginView(primaryStage)
    }

}
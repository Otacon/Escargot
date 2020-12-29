package features

import features.login.LoginView
import javafx.application.Application
import javafx.stage.Stage

lateinit var appInstance: Application

class EscargotApplication: Application() {

    override fun init() {
        super.init()
        appInstance = this
    }

    override fun start(primaryStage: Stage) {
        LoginView.launch(primaryStage)
    }

}
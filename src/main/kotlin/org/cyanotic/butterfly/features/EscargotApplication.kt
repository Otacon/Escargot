package org.cyanotic.butterfly.features

import javafx.application.Application
import javafx.stage.Stage
import org.cyanotic.butterfly.features.login.LoginView

lateinit var appInstance: Application

class EscargotApplication : Application() {

    override fun init() {
        super.init()
        appInstance = this
    }

    override fun start(primaryStage: Stage) {
        LoginView.launch(primaryStage)
    }

}
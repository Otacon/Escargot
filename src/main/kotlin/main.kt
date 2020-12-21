import features.EscargotApplication
import javafx.application.Application.launch


fun main(args: Array<String>) {
    try {
        launch(EscargotApplication::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


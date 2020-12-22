import features.EscargotApplication
import javafx.application.Application.launch


fun main() {
    try {
        launch(EscargotApplication::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
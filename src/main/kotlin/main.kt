import database.MSNDB
import features.EscargotApplication
import javafx.application.Application.launch


fun main() {
    MSNDB.db.accountsQueries.clearTemporaryAccounts()
    try {
        launch(EscargotApplication::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
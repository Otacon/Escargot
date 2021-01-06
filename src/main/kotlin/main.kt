import database.AccountsTable
import features.EscargotApplication
import javafx.application.Application.launch


fun main() {
    AccountsTable().apply { clearTemporaryAccounts() }
    try {
        launch(EscargotApplication::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
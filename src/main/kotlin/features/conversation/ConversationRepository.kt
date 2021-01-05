package features.conversation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import database.MSNDB
import kotlinx.coroutines.flow.Flow
import me.orfeo.Message

class ConversationRepository {

    suspend fun newMessages(recipient: String): Flow<List<Message>> {
        val conversationId = getConversationId(recipient)
        val database = MSNDB.db
        return database.messagesQueries.getAllByConversationId(conversationId).asFlow().mapToList()
    }

    suspend fun sendMessage(message: String, recipient: String) {
        val database = MSNDB.db
        val conversationId = getConversationId(recipient)
        val myPassport = database.accountsQueries.getCurrent().executeAsOne().passport
        database.messagesQueries.add(conversationId, myPassport, System.currentTimeMillis(), message, false)
    }

    private fun getConversationId(recipient: String): Long {
        val database = MSNDB.db
        val myPassport = database.accountsQueries.getCurrent().executeAsOne().passport
        var conv = database.conversationQueries.getByAccountRecipient(myPassport, recipient).executeAsOneOrNull()
        if (conv == null) {
            database.conversationQueries.create(myPassport, recipient)
            conv = MSNDB.db.conversationQueries.getByAccountRecipient(myPassport, recipient).executeAsOne()
        }
        return conv.id
    }
}
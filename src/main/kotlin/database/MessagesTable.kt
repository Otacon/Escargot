package database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.*
import me.orfeo.Message

class MessagesTable {

    private val queries = MSNDB.db.messagesQueries

    fun newConversationMessages(conversationId: Long): Flow<Message> {
        return queries.selectNewConversationMessages(conversationId)
            .asFlow()
            .mapToOneNotNull()
            .map {
                Message(
                    id = it.id,
                    conversation_id = it.conversation_id,
                    sender = it.sender,
                    timestamp = it.timestamp,
                    text = it.text
                )
            }
    }

    fun addMessage(conversationId: Long, sender: String, timestamp: Long, message: String) {
        queries.transaction {
            queries.add(
                conversation_id = conversationId,
                sender = sender,
                timestamp = timestamp,
                text = message
            )
        }
    }

    fun newOtherMessages(account: String): Flow<Message> {
        return queries.selectNewOtherMessages(account).asFlow().mapToOneNotNull().map {
            Message(
                id = it.id,
                conversation_id = it.conversation_id,
                sender = it.sender,
                timestamp = it.timestamp,
                text = it.text
            )
        }
    }
}
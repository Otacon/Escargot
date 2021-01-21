package org.cyanotic.butterfly.database

import org.cyanotic.butterfly.database.entities.Conversation
import org.cyanotic.butterfly.database.entities.ConversationQueries

class ConversationsTable(
    private val queries: ConversationQueries
) {

    suspend fun getConversationById(id: Long): Conversation {
        return queries.getById(id).executeAsOne()

    }

    suspend fun getConversationByRecipient(account: String, recipient: String): Conversation? {
        return queries.getByAccountRecipient(account, recipient).executeAsOneOrNull()

    }

    suspend fun createConversation(account: String, other: String): Conversation {
        queries.create(account, other, 0)
        val lastId = queries.lastInsertedId().executeAsOne()
        return queries.getById(lastId).executeAsOne()
    }

    suspend fun markAsRead(conversationId: Long) {
        queries.setLastRead(System.currentTimeMillis(), conversationId)
    }

}
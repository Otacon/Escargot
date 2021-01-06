package database

import me.orfeo.Conversation

class ConversationsTable {

    private val queries = MSNDB.db.conversationQueries

    suspend fun getConversationById(id: Long): Conversation {
        return queries.transactionWithResult {
            queries.getById(id).executeAsOne()
        }
    }

    suspend fun getConversationByRecipient(account: String, recipient: String): Conversation? {
        return queries.transactionWithResult {
            queries.getByAccountRecipient(account, recipient).executeAsOneOrNull()
        }
    }

    suspend fun createConversation(account: String, other: String): Conversation {
        return queries.transactionWithResult {
            queries.create(account, other, 0)
            val lastId = queries.lastInsertedId().executeAsOne()
            queries.getById(lastId).executeAsOne()
        }

    }

}
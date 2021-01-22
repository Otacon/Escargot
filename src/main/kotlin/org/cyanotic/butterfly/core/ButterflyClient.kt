package org.cyanotic.butterfly.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.cyanotic.butterfly.core.auth.AuthenticationResult
import org.cyanotic.butterfly.core.auth.MSNPAuthenticator
import org.cyanotic.butterfly.core.auth.MSNPAuthenticatorFactory
import org.cyanotic.butterfly.core.contact_list_fetcher.ContactListFetcher
import org.cyanotic.butterfly.core.file_manager.fileManager
import org.cyanotic.butterfly.core.utils.httpClient
import org.cyanotic.butterfly.database.MsnDB
import org.cyanotic.butterfly.protocol.Endpoints
import org.cyanotic.butterfly.protocol.ProtocolVersion
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger("ButterflyClient")

object ButterflyClient : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val actor = GlobalScope.actor<ClientOperation> {
        var authenticator: MSNPAuthenticator? = null
        var accounts: AccountManager? = null
        var contacts: ContactManager? = null
        var conversation: ConversationManager? = null
        var notification: NotificationTransport? = null
        var database: MsnDB? = null
        for (msg in channel) {
            when (msg) {
                is ClientOperation.Connect -> {
                    if (notification != null) {
                        msg.continuation.completeExceptionally(IllegalStateException("You are already connected."))
                    }
                    notification = NotificationTransport().apply {
                        connect(msg.endpoint, msg.port)
                        val verResponse = sendVer(protocols = listOf(ProtocolVersion.MSNP18))
                        if (verResponse.protocols.size == 1 && verResponse.protocols[0] == ProtocolVersion.UNKNOWN) {
                            disconnect()
                            msg.continuation.complete(ConnectResult.ProtocolHandshakeFailure)
                        }
                        authenticator = MSNPAuthenticatorFactory().createAuthenticator(this)
                        msg.continuation.complete(ConnectResult.Success)
                    }
                }
                ClientOperation.Disconnect -> {
                    logger.info { "Disconnecting notification..." }
                    notification?.disconnect()
                    notification = null
                    accounts = null
                    contacts = null
                    logger.info { "Disconnecting all switchboards..." }
                    conversation?.closeAll()
                    conversation = null
                    authenticator = null
                    logger.info { "Closing database..." }
                    database?.close()
                    database = null
                    logger.info { "All resources released." }
                }
                is ClientOperation.Authenticate -> {
                    authenticator.let {
                        if (it == null) {
                            msg.continuation.completeExceptionally(IllegalStateException("Trying to authenticate when already connected or disconnected"))
                        } else {
                            val result = it.authenticate(username = msg.username, password = msg.password)
                            when (result) {
                                AuthenticationResult.InvalidPassword -> {
                                    launch(Dispatchers.IO) { disconnect() }
                                }
                                AuthenticationResult.InvalidUser -> {
                                    launch(Dispatchers.IO) { disconnect() }
                                }
                                AuthenticationResult.ServerError -> {
                                    launch(Dispatchers.IO) { disconnect() }
                                }
                                is AuthenticationResult.Success -> {
                                    logger.info { "Initialising user database" }
                                    val db = MsnDB(path = fileManager.getAccountFolder(msg.username))
                                    logger.info { "Initialising AccountManager..." }
                                    val accountManager = AccountManager(
                                        account = msg.username,
                                        mspAuth = result.token,
                                        notification = notification!!
                                    )
                                    logger.info { "Initialising ContactManager..." }
                                    val contactManager = ContactManager(
                                        accountManager = accountManager,
                                        localContacts = db.contacts,
                                        notification = notification,
                                        contactListFetcher = ContactListFetcher(httpClient)
                                    )
                                    logger.info { "Initialising ConversationManager..." }
                                    val conversationManager = ConversationManager(
                                        accountManager = accountManager,
                                        notification = notification
                                    )
                                    launch(Dispatchers.IO){
                                        notification.switchboardInvites().collect { invite ->
                                            val conversation = conversationManager.getConversation(invite.passport)
                                            conversation.inviteReceived(invite)
                                        }
                                    }

                                    accounts = accountManager
                                    contacts = contactManager
                                    conversation = conversationManager
                                    database = db
                                }
                            }
                            msg.continuation.complete(result)
                        }
                    }
                }
                is ClientOperation.GetAccountManager -> {
                    accounts.let {
                        if (it == null) {
                            msg.continuation.completeExceptionally(IllegalStateException("Account Manager is not available. Make sure you are connected and authenticated."))
                        } else {
                            msg.continuation.complete(it)
                        }

                    }
                }
                is ClientOperation.GetContactManager -> {
                    contacts.let {
                        if (it == null) {
                            msg.continuation.completeExceptionally(IllegalStateException("Contact Manager is not available. Make sure you are connected and authenticated."))
                        } else {
                            msg.continuation.complete(it)
                        }

                    }
                }
                is ClientOperation.GetConversationManager -> {
                    conversation.let {
                        if (it == null) {
                            msg.continuation.completeExceptionally(IllegalStateException("Conversation Manager is not available. Make sure you are connected and authenticated."))
                        } else {
                            msg.continuation.complete(it)
                        }

                    }
                }
            }
        }
        logger.error { "Client's actor is closed!!!" }
    }


    suspend fun connect(
        endpoint: String = Endpoints.notificationAddress,
        port: Int = Endpoints.notificationPort
    ): ConnectResult {
        val continuation = CompletableDeferred<ConnectResult>()
        val operation = ClientOperation.Connect(endpoint, port, continuation)
        actor.send(operation)
        return continuation.await()
    }

    suspend fun authenticate(username: String, password: String): AuthenticationResult {
        val continuation = CompletableDeferred<AuthenticationResult>()
        val operation = ClientOperation.Authenticate(username, password, continuation)
        actor.send(operation)
        return continuation.await()
    }

    suspend fun disconnect() {
        val continuation = CompletableDeferred<Unit>()
        val operation = ClientOperation.Disconnect
        actor.send(operation)
        continuation.await()
    }

    suspend fun getAccountManager(): AccountManager {
        val continuation = CompletableDeferred<AccountManager>()
        val operation = ClientOperation.GetAccountManager(continuation)
        actor.send(operation)
        return continuation.await()
    }

    suspend fun getContactManager(): ContactManager {
        val continuation = CompletableDeferred<ContactManager>()
        val operation = ClientOperation.GetContactManager(continuation)
        actor.send(operation)
        return continuation.await()
    }

    suspend fun getConversationManager(): ConversationManager {
        val continuation = CompletableDeferred<ConversationManager>()
        val operation = ClientOperation.GetConversationManager(continuation)
        actor.send(operation)
        return continuation.await()
    }

}

sealed class ClientOperation {
    data class Connect(
        val endpoint: String,
        val port: Int,
        val continuation: CompletableDeferred<ConnectResult>
    ) : ClientOperation()

    data class Authenticate(
        val username: String,
        val password: String,
        val continuation: CompletableDeferred<AuthenticationResult>
    ) : ClientOperation()

    object Disconnect : ClientOperation()

    data class GetAccountManager(
        val continuation: CompletableDeferred<AccountManager>
    ) : ClientOperation()

    data class GetContactManager(
        val continuation: CompletableDeferred<ContactManager>
    ) : ClientOperation()

    data class GetConversationManager(
        val continuation: CompletableDeferred<ConversationManager>
    ) : ClientOperation()
}

sealed class ConnectResult {
    object Success : ConnectResult()
    object Failure : ConnectResult()
    object ProtocolHandshakeFailure : ConnectResult()
}
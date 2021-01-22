package org.cyanotic.butterfly.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.cyanotic.butterfly.protocol.Status
import org.cyanotic.butterfly.protocol.asStatus
import org.cyanotic.butterfly.protocol.asString
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import org.cyanotic.butterfly.protocol.notification.TransportException
import kotlin.coroutines.CoroutineContext


private val logger = KotlinLogging.logger(name = "AccountManager")

class AccountManager(
    val account: String,
    val mspAuth: String,
    private val notification: NotificationTransport
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private var status = Status.OFFLINE
    private var nickname = account
    private var personalMessage = ""

    private val accountUpdateChannel = BroadcastChannel<AccountUpdate>(Channel.CONFLATED)

    init {
        listenForAccountChanges()
    }

    fun getStatus() = status

    fun getNickname() = nickname

    fun getPersonalMessage() = personalMessage

    suspend fun setStatus(status: Status) {
        logger.info { "Setting status to $status" }
        val oldStatus = this.status
        this.status = status
        triggerAccountUpdate()
        try {
            if (status != Status.OFFLINE) {
                notification.sendChg(status.asString())
            } else {
                val networkId = ""
                notification.sendFln(account, networkId)
            }
        } catch (e: TransportException) {
            logger.error { "Failed to set status to $status" }
            this.status = oldStatus
            triggerAccountUpdate()
            e.printStackTrace()
        }
    }

    suspend fun setNickname(nickname: String) {
        logger.info { "Setting nickname to $nickname" }
        val oldNickname = nickname
        this.nickname = nickname
        triggerAccountUpdate()
        try {
            //TODO update nickname
        } catch (e: TransportException) {
            logger.error { "Failed to set nickname to $nickname" }
            this.nickname = oldNickname
            triggerAccountUpdate()
            e.printStackTrace()
        }
    }

    fun accountUpdates() = accountUpdateChannel.asFlow()

    suspend fun setPersonalMessage(personalMessage: String) {
        logger.info { "Setting personal message to $personalMessage" }
        val oldPersonalMessage = this.personalMessage
        this.personalMessage = personalMessage
        triggerAccountUpdate()
        try {
            notification.sendUux(personalMessage)
        } catch (e: TransportException) {
            logger.error { "Failed to set personal message to $personalMessage" }
            this.personalMessage = oldPersonalMessage
            triggerAccountUpdate()
        }
    }

    private fun listenForAccountChanges() {
        launch {
            notification.contactChanged().collect { profileData ->
                logger.debug { "Account Changed $profileData" }
                if (profileData.passport.equals(account, true)) {
                    status = profileData.status?.asStatus() ?: status
                    nickname = profileData.nickname ?: nickname
                    personalMessage = profileData.personalMessage ?: personalMessage
                    triggerAccountUpdate()
                }
            }
        }
    }

    private fun triggerAccountUpdate() {
        val accountUpdate = AccountUpdate(
            status = this.status,
            nickname = this.nickname,
            personalMessage = this.personalMessage
        )
        logger.debug { "Account Updated: $accountUpdate" }
        accountUpdateChannel.offer(accountUpdate)
    }

}

data class AccountUpdate(
    val status: Status,
    val nickname: String,
    val personalMessage: String
)
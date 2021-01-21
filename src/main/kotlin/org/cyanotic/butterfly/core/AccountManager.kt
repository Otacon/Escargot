package org.cyanotic.butterfly.core

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import mu.KotlinLogging
import org.cyanotic.butterfly.protocol.Status
import org.cyanotic.butterfly.protocol.asString
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import org.cyanotic.butterfly.protocol.notification.TransportException


private val logger = KotlinLogging.logger(name = "AccountManager")

class AccountManager(
    val account: String,
    val mspAuth: String,
    private val notification: NotificationTransport
) {

    private var status = Status.OFFLINE
    private var nickname = account
    private var personalMessage = ""

    private val statusChannel = BroadcastChannel<AccountUpdate>(Channel.CONFLATED)
    val accountUpdates = statusChannel
        .openSubscription()
        .consumeAsFlow()

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

    //TODO this has to be hidden to the user, somehow.
    suspend fun accountUpdated(status: Status? = null, nickname: String? = null, personalMessage: String? = null) {
        status?.let { this.status = status }
        nickname?.let { this.nickname = nickname }
        personalMessage?.let { this.personalMessage = personalMessage }
        logger.debug { "Account Updated: Status=${this.status}, Nickname=${this.nickname}, Personal Message=${this.personalMessage}" }
        triggerAccountUpdate()
    }

    private fun triggerAccountUpdate() {
        statusChannel.offer(
            AccountUpdate(
                status = this.status,
                nickname = this.nickname,
                personalMessage = this.personalMessage
            )
        )
    }

}

data class AccountUpdate(
    val status: Status,
    val nickname: String,
    val personalMessage: String
)
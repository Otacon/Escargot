package org.cyanotic.butterfly.features.notifications

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object NotificationManager {

    private val newMessage: MediaPlayer by lazy { loadMediaPlayer("/sounds/message.mp3") }

    private val nudge: MediaPlayer by lazy { loadMediaPlayer("/sounds/message.mp3") }

    private val contactOnline: MediaPlayer by lazy { loadMediaPlayer("/sounds/message.mp3") }

    var notificationsEnabled = true

    fun newMessage() {
        logger.info { "Playing new message sound" }
        newMessage.playSound()
    }

    fun contactOnline() {
        logger.info { "Playing contact online sound" }
        contactOnline.playSound()
    }

    fun nudge() {
        logger.info { "Playing nudge sound" }
        nudge.playSound()
    }

    private fun loadMediaPlayer(soundResource: String): MediaPlayer {
        val file = javaClass.getResource(soundResource).toString()
        return MediaPlayer(Media(file))
    }

    private fun MediaPlayer.playSound() {
        if (notificationsEnabled) {
            stop()
            play()
        }
    }
}
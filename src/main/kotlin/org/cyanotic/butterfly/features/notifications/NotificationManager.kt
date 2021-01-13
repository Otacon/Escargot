package org.cyanotic.butterfly.features.notifications

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer

object NotificationManager {

    private val newMessage: MediaPlayer by lazy { loadMediaPlayer("/message.mp3") }

    private val nudge: MediaPlayer by lazy { loadMediaPlayer("/message.mp3") }

    private val contactOnline: MediaPlayer by lazy { loadMediaPlayer("/message.mp3") }

    var notificationsEnabled = true

    fun newMessage() {
        newMessage.playSound()
    }

    fun contactOnline() {
        contactOnline.playSound()
    }

    fun nudge() {
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
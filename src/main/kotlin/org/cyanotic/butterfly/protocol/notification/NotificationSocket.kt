package org.cyanotic.butterfly.protocol.notification

import mu.KotlinLogging
import java.io.BufferedInputStream
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketException
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger("Notification")

class NotificationSocket {

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedInputStream

    fun connect(endpoint: String, port: Int) {
        logger.info("Connecting to $endpoint:$port...")
        socket = Socket(endpoint, port)
        writer = PrintWriter(socket.outputStream)
        reader = BufferedInputStream(socket.inputStream)
        logger.info("Done!")
    }

    fun sendMessage(message: String, sendNewLine: Boolean = true) {
        writer.write(message)
        if (sendNewLine) {
            writer.write("\r\n")
        }
        writer.flush()
        logger.debug { ">> $message" }
    }

    fun readMessage(): String? {
        val response = reader.readLine()
        response?.let { logger.debug { "<< $response" } }
        return response
    }

    fun readRaw(length: Int): String {
        val output = String(reader.readNBytes(length), StandardCharsets.UTF_8)
        logger.debug { "<< $output" }
        return output
    }

    fun close() {
        logger.info("Closing connection...")
        writer.close()
        reader.close()
        socket.close()
        logger.info("Done!")
    }
}

private fun BufferedInputStream.readLine(): String? {
    return try {
        val buffer = ByteArray(128_000)
        var index = 0
        do {
            val lastByte = read()
            buffer[index] = lastByte.toByte()
            index++

        } while (lastByte != 0x0A)
        String(buffer, 0, index - 2, StandardCharsets.UTF_8)
    } catch (e: SocketException) {
        null
    }
}
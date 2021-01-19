package org.cyanotic.butterfly.protocol.notification

import mu.KotlinLogging
import org.cyanotic.butterfly.protocol.Endpoints
import java.io.BufferedInputStream
import java.io.PrintWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

class NotificationSocket {

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedInputStream

    fun connect(endpoint: String = Endpoints.notificationAddress, port: Int = Endpoints.notificationPort) {
        logger.info("NT: Connecting to $endpoint:$port...")
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
        logger.info("NT >> $message")
    }

    fun readMessage(): String {
        val response = reader.readLine()
        logger.info("NT << $response")
        return response
    }

    fun readRaw(length: Int): String {
        val output = String(reader.readNBytes(length), StandardCharsets.UTF_8)
        logger.info("NT << $output")
        return output
    }

    fun close() {
        logger.info("NT Closing connection...")
        writer.close()
        reader.close()
        socket.close()
        logger.info("Done!")
    }
}

private fun BufferedInputStream.readLine(): String {
    val buffer = ByteArray(128_000)
    var index = 0
    do {
        val lastByte = read()
        buffer[index] = lastByte.toByte()
        index++
    } while (lastByte != 0x0A)
    return String(buffer, 0, index - 2, StandardCharsets.UTF_8)
}
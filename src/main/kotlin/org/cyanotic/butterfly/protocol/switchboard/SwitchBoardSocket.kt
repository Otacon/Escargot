package org.cyanotic.butterfly.protocol.switchboard

import mu.KotlinLogging
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

private val logger = KotlinLogging.logger("Switchboard")

class SwitchBoardSocket {

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader

    fun connect(endpoint: String, port: Int) {
        logger.info { "SB Connecting to $endpoint:$port..." }
        socket = Socket(endpoint, port)
        writer = PrintWriter(socket.outputStream)
        reader = BufferedReader(InputStreamReader(socket.inputStream))
        logger.info { "Done!" }
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
        print("SB << ")
        var output = ""
        var remaining = length
        while (remaining > 0) {
            val buffer = CharArray(remaining)
            remaining -= reader.read(buffer)
            output += String(buffer)
        }
        logger.debug { "<< $output" }
        return output
    }

    fun close() {
        logger.info{"Closing connection..."}
        writer.close()
        reader.close()
        socket.close()
        logger.info{"Done!"}
    }
}
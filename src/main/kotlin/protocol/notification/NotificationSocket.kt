package protocol.notification

import protocol.Endpoints
import java.io.BufferedInputStream
import java.io.PrintWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

class NotificationSocket {

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedInputStream

    fun connect(endpoint: String = Endpoints.notificationAddress, port: Int = Endpoints.notificationPort) {
        print("NT: Connecting to $endpoint:$port...")
        socket = Socket(endpoint, port)
        writer = PrintWriter(socket.outputStream)
        reader = BufferedInputStream(socket.inputStream)
        println("Done!")
    }

    fun sendMessage(message: String) {
        print("NT >> ")
        writer.write("$message\r\n")
        writer.flush()
        println(message)
    }

    fun readMessage(): String {
        val response = reader.readLine()
        println("NT << $response")
        return response
    }

    fun readRaw(length: Int): String {
        print("NT << ")
        val output = String(reader.readNBytes(length), StandardCharsets.UTF_8)
        println(output)
        return output
    }

    fun close() {
        print("NT Closing connection...")
        writer.close()
        reader.close()
        socket.close()
        println("Done!")
    }
}

private fun BufferedInputStream.readLine(): String {
    var output = ""
    while (!output.endsWith("\r\n")) {
        val char = this.read().toChar()
        output += char
    }
    return output.trim()
}
package protocol.notification

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class NotificationSocket {

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader

    fun connect(endpoint: String = "35.185.200.209", port: Int = 1863) {
        print("NT: Connecting to $endpoint:$port...")
        socket = Socket(endpoint, port)
        writer = PrintWriter(socket.outputStream)
        reader = BufferedReader(InputStreamReader(socket.inputStream))
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
        var output = ""
        var remaining = length
        while (remaining > 0) {
            val buffer = CharArray(remaining)
            remaining -= reader.read(buffer)
            output += String(buffer)
        }
        println(output)
        return output
    }

    @Deprecated("For some reasons the server returns the wrong length.")
    fun readUBXBody(): String {
        print("NT << ")
        var output = ""
        while (!output.endsWith("</Data>")) {
            val char = reader.read().toChar()
            output += char
        }
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